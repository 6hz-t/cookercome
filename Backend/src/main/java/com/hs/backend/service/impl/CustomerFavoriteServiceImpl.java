package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.dto.response.FavoriteChefResponse;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.entity.CustomerFavorite;
import com.hs.backend.entity.User;
import com.hs.backend.mapper.ChefInfoMapper;
import com.hs.backend.mapper.CustomerFavoriteMapper;
import com.hs.backend.mapper.UserMapper;
import com.hs.backend.service.CustomerFavoriteService;
import com.hs.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerFavoriteServiceImpl implements CustomerFavoriteService {
    
    private final CustomerFavoriteMapper customerFavoriteMapper;
    private final ChefInfoMapper chefInfoMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Redis 缓存 Key 前缀
    private static final String FAVORITE_LIST_CACHE_KEY = "user:favorites:list:";
    private static final long FAVORITE_LIST_CACHE_EXPIRE_MINUTES = 30L; // 收藏列表缓存 30 分钟
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFavorite(Long customerId, String chefId) {
        // 检查是否已收藏
        if (customerFavoriteMapper.exists(customerId, chefId)) {
            throw new BusinessException("该厨师已在收藏夹中");
        }
        
        // 创建收藏记录
        CustomerFavorite favorite = new CustomerFavorite();
        favorite.setCustomerId(customerId);
        favorite.setChefId(chefId);
        favorite.setCreateTime(LocalDateTime.now());
        
        int rows = customerFavoriteMapper.insert(favorite);
        if (rows <= 0) {
            throw new BusinessException("添加收藏失败");
        }
        
        // 清除用户的订单缓存（因为订单列表中的收藏状态会变化）
        clearUserOrdersCache(customerId);
        
        // 清除用户的收藏缓存
        clearUserFavoritesCache(customerId);
        
        log.info("用户 {} 收藏了厨师 {}，已清除相关缓存", customerId, chefId);
    }
    
    /**
     * 清除用户的所有订单缓存
     */
    private void clearUserOrdersCache(Long userId) {
        // 清除所有分类的订单缓存
        java.util.List<String> categories = java.util.List.of("all", "pending", "payment", "fulfillment", "refunding", "history");
        for (String category : categories) {
            String cacheKey = "user:orders:" + category + ":" + userId;
            redisTemplate.delete(cacheKey);
            log.debug("已清除用户 {} 的 {} 订单缓存", userId, category);
        }
    }
    
    /**
     * 清除用户的收藏缓存（包括收藏列表和收藏状态缓存）
     */
    private void clearUserFavoritesCache(Long userId) {
        // 清除收藏列表缓存
        String listCacheKey = FAVORITE_LIST_CACHE_KEY + userId;
        redisTemplate.delete(listCacheKey);
        log.debug("已清除用户 {} 的收藏列表缓存", userId);
        
        // 清除简单的收藏状态缓存（如果存在）
        String statusCacheKey = "user:favorites:" + userId;
        redisTemplate.delete(statusCacheKey);
        log.debug("已清除用户 {} 的收藏状态缓存", userId);
    }
    
    @Override
    public boolean isFavorited(Long customerId, String chefId) {
        return customerFavoriteMapper.exists(customerId, chefId);
    }
    
    @Override
    public List<FavoriteChefResponse> getFavoriteList(Long customerId) {
        // 构建缓存 key
        String cacheKey = FAVORITE_LIST_CACHE_KEY + customerId;
        
        // 1. 先尝试从 Redis 获取缓存
        try {
            Object cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData instanceof List) {
                log.debug("从 Redis 缓存中获取到用户 {} 的收藏列表", customerId);
                return (List<FavoriteChefResponse>) cachedData;
            }
        } catch (Exception e) {
            log.warn("Redis 缓存读取失败，降级到数据库查询：{}", e.getMessage());
        }
        
        log.debug("未命中缓存，从数据库查询用户 {} 的收藏列表", customerId);
        
        // 2. 缓存未命中，查询数据库
        // 查询用户的收藏记录
        QueryWrapper<CustomerFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_id", customerId)
                    .orderByDesc("create_time");
        List<CustomerFavorite> favorites = customerFavoriteMapper.selectList(queryWrapper);
        
        if (favorites.isEmpty()) {
            // 空列表也缓存，避免穿透，缓存 5 分钟
            try {
                redisTemplate.opsForValue().set(cacheKey, List.of(), 5, java.util.concurrent.TimeUnit.MINUTES);
            } catch (Exception e) {
                log.warn("Redis 缓存写入失败：{}", e.getMessage());
            }
            return List.of();
        }
        
        // 获取厨师 ID 列表（favorite 表的 chef_id 是 chefinfo 表的 user_id）
        List<String> chefUserIds = favorites.stream()
                .map(CustomerFavorite::getChefId)
                .collect(Collectors.toList());
        
        log.debug("用户 {} 的收藏厨师 userid 列表：{}", customerId, chefUserIds);
        
        // 批量查询厨师信息（通过 chefinfo 表的 user_id）
        QueryWrapper<ChefInfo> chefQueryWrapper = new QueryWrapper<>();
        chefQueryWrapper.in("user_id", chefUserIds)
                .eq("audit_status", 1) // 只查询审核通过的厨师
                .eq("status", 1); // 只查询启用状态的厨师
        List<ChefInfo> chefs = chefInfoMapper.selectList(chefQueryWrapper);
        
        log.debug("用户 {} 查询到的厨师数量：{}", customerId, chefs.size());
        
        // 转换为响应 DTO
        List<FavoriteChefResponse> responses = chefs.stream()
                .map(chef -> {
                    FavoriteChefResponse response = new FavoriteChefResponse();
                    response.setChefId(chef.getUserId()); // 返回 userid 而不是 id
                    response.setRealName(chef.getRealName());
                    response.setGender(chef.getGender());
                    response.setChefLevel(chef.getChefLevel());
                    response.setExperienceYears(chef.getExperienceYears());
                    response.setCompletedOrders(chef.getCompletedOrders());
                    response.setMinPrice(chef.getMinPrice());
                    response.setIntroduction(chef.getIntroduction());
                    response.setAvatarUrl(chef.getAvatarUrl());
                    
                    // 从 User 表获取手机号
                    User user = userMapper.selectById(Long.parseLong(chef.getUserId()));
                    if (user != null) {
                        response.setPhone(user.getPhone());
                    }
                    
                    // 设置收藏时间
                    CustomerFavorite favorite = favorites.stream()
                            .filter(f -> f.getChefId().equals(chef.getUserId()))
                            .findFirst()
                            .orElse(null);
                    if (favorite != null) {
                        response.setFavoriteTime(favorite.getCreateTime());
                    }
                    
                    return response;
                })
                .collect(Collectors.toList());
        
        // 3. 将结果存入 Redis 缓存
        try {
            redisTemplate.opsForValue().set(cacheKey, responses, FAVORITE_LIST_CACHE_EXPIRE_MINUTES, java.util.concurrent.TimeUnit.MINUTES);
            log.debug("收藏列表数据已存入 Redis 缓存，key: {}, 有效期 {} 分钟", cacheKey, FAVORITE_LIST_CACHE_EXPIRE_MINUTES);
        } catch (Exception e) {
            log.warn("Redis 缓存写入失败：{}", e.getMessage());
        }
        
        return responses;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(Long customerId, String chefId) {
        // 查询收藏记录
        QueryWrapper<CustomerFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_id", customerId)
                    .eq("chef_id", chefId);
        CustomerFavorite favorite = customerFavoriteMapper.selectOne(queryWrapper);
        
        if (favorite == null) {
            throw new BusinessException("未找到该收藏记录");
        }
        
        // 删除收藏记录
        int rows = customerFavoriteMapper.delete(queryWrapper);
        if (rows <= 0) {
            throw new BusinessException("取消收藏失败");
        }
        
        // 清除用户的订单缓存
        clearUserOrdersCache(customerId);
        
        // 清除用户的收藏缓存
        clearUserFavoritesCache(customerId);
        
        log.info("用户 {} 取消了收藏厨师 {}，已清除相关缓存", customerId, chefId);
    }
}
