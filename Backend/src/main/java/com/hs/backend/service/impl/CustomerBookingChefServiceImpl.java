package com.hs.backend.service.impl;

import com.aliyun.oss.OSS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hs.backend.common.config.OssConfig;
import com.hs.backend.dto.response.CustomerBookingChefResponse;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.entity.User;
import com.hs.backend.mapper.ChefInfoMapper;
import com.hs.backend.service.CustomerBookingChefService;
import com.hs.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 客户预约厨师服务实现类
 */
@Slf4j
@Service
public class CustomerBookingChefServiceImpl extends ServiceImpl<ChefInfoMapper, ChefInfo> implements CustomerBookingChefService {
    
    private final OSS ossClient;
    private final OssConfig ossConfig;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    
    // Redis Key 前缀
    private static final String CHEF_LIST_CACHE_KEY = "chef:list:";
    private static final String CHEF_COUNT_CACHE_KEY = "chef:count:";
    private static final long CACHE_EXPIRE_MINUTES = 10L; // 缓存有效期 10 分钟
    
    public CustomerBookingChefServiceImpl(OSS ossClient, OssConfig ossConfig, UserService userService, 
                                          RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.ossClient = ossClient;
        this.ossConfig = ossConfig;
        this.userService = userService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public Page<CustomerBookingChefResponse> getChefPage(Integer page, Integer size, String sortBy, String name) {
        // 构建缓存 key（包含所有查询参数）
        String cacheKey = buildCacheKey(page, size, sortBy, name);
        
        // 1. 先尝试从 Redis 获取缓存
        try {
            Object cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData != null) {
                log.debug("命中缓存：{}", cacheKey);
                return (Page<CustomerBookingChefResponse>) cachedData;
            }
        } catch (Exception e) {
            log.warn("Redis 缓存读取失败，降级到数据库查询：{}", e.getMessage());
        }
        
        log.debug("未命中缓存，查询数据库：{}", cacheKey);
        
        // 2. 缓存未命中，查询数据库
        Page<ChefInfo> chefPage = new Page<>(page, size);
        
        // 构建查询条件
        LambdaQueryWrapper<ChefInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChefInfo::getAuditStatus, 1)  // 只查询已审核通过的
                .eq(ChefInfo::getStatus, 1); // 只查询启用的
        
        // 按名字搜索（模糊匹配）
        if (StringUtils.hasText(name)) {
            wrapper.like(ChefInfo::getRealName, name);
        }
        
        // 排序逻辑 - 根据实际字段调整
        if (StringUtils.hasText(sortBy)) {
            switch (sortBy) {
                case "level_desc":
                    // 按厨师等级降序
                    wrapper.orderByDesc(ChefInfo::getChefLevel);
                    break;
                case "price_asc":
                    // 按最低金额升序
                    wrapper.orderByAsc(ChefInfo::getMinPrice);
                    break;
                case "orders_desc":
                    // 按完成订单数降序
                    wrapper.orderByDesc(ChefInfo::getCompletedOrders);
                    break;
                default:
                    // 默认排序：按 ID 降序
                    wrapper.orderByDesc(ChefInfo::getId);
            }
        } else {
            // 默认排序
            wrapper.orderByDesc(ChefInfo::getId);
        }
        
        // 执行分页查询
        Page<ChefInfo> resultPage = page(chefPage, wrapper);
        
        // 转换为 CustomerBookingChefResponse 并设置头像预签名 URL
        Page<CustomerBookingChefResponse> responsePage = new Page<>(page, size, resultPage.getTotal());
        List<CustomerBookingChefResponse> responses = resultPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        responsePage.setRecords(responses);
        
        // 3. 将结果存入 Redis 缓存
        try {
            redisTemplate.opsForValue().set(cacheKey, responsePage, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            log.debug("缓存厨师列表数据：{}, 有效期 {} 分钟", cacheKey, CACHE_EXPIRE_MINUTES);
        } catch (Exception e) {
            log.warn("Redis 缓存写入失败：{}", e.getMessage());
        }
        
        return responsePage;
    }
    
    /**
     * 构建缓存 Key（包含所有查询参数）
     */
    private String buildCacheKey(Integer page, Integer size, String sortBy, String name) {
        StringBuilder keyBuilder = new StringBuilder(CHEF_LIST_CACHE_KEY);
        keyBuilder.append("p").append(page)
                  .append("_s").append(size);
        
        if (StringUtils.hasText(sortBy) && !"default".equals(sortBy)) {
            keyBuilder.append("_sort").append(sortBy);
        }
        
        if (StringUtils.hasText(name)) {
            keyBuilder.append("_name").append(name);
        }
        
        return keyBuilder.toString();
    }
    
    /**
     * 转换 ChefInfo 为 CustomerBookingChefResponse，并生成头像预签名 URL
     */
    private CustomerBookingChefResponse convertToResponse(ChefInfo chefInfo) {
        CustomerBookingChefResponse response = new CustomerBookingChefResponse();
        
        // 使用 BeanUtils 复制相同名称的字段
        BeanUtils.copyProperties(chefInfo, response);
        
        // 设置 id 为 user_id 而不是 chef_info 的主键 id
        response.setId(Long.parseLong(chefInfo.getUserId()));
        
        // 设置手机号（从 User 表获取，如果 ChefInfo 中没有）
        if (chefInfo.getPhone() == null || chefInfo.getPhone().isEmpty()) {
            User user = userService.getById(chefInfo.getUserId());
            if (user != null) {
                response.setPhone(user.getPhone());
            }
        }
        
        // 生成头像预签名 URL（使用 avatarUrl 字段）
        String avatar = chefInfo.getAvatarUrl();
        
        if (avatar != null && !avatar.isEmpty() && !avatar.startsWith("http")) {
            try {
                // 生成预签名 URL（有效期 30 天 = 43200 分钟）
                Date expiration = new Date(System.currentTimeMillis() + 43200L * 60 * 1000);
                java.net.URL signedUrl = ossClient.generatePresignedUrl(
                    ossConfig.getBucketName(), 
                    avatar, 
                    expiration
                );
                response.setAvatarUrl(signedUrl.toString());
                log.debug("生成厨师头像预签名 URL: {}", signedUrl.toString());
            } catch (Exception e) {
                log.warn("生成预签名 URL 失败，使用普通 URL: {}", e.getMessage());
                // 降级方案：使用普通 URL
                String fullUrl = "https://" + ossConfig.getBucketName() + "." + 
                                ossConfig.getEndpoint().replace("https://", "").replace("http://", "") + "/" + avatar;
                response.setAvatarUrl(fullUrl);
            }
        } else if (avatar != null && avatar.startsWith("http")) {
            response.setAvatarUrl(avatar);
        }
        
        return response;
    }
    
    /**
     * 清除厨师列表缓存（当厨师信息变更时调用）
     */
    public void clearChefListCache() {
        try {
            // 清除所有以 chef:list:开头的缓存 key
            redisTemplate.delete(redisTemplate.keys(CHEF_LIST_CACHE_KEY + "*"));
            log.info("清除厨师列表缓存");
        } catch (Exception e) {
            log.warn("清除缓存失败：{}", e.getMessage());
        }
    }
}
