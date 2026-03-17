package com.hs.backend.service.impl;

import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.entity.*;
import com.hs.backend.mapper.CustomerInfoMapper;
import com.hs.backend.mapper.UserAddressMapper;
import com.hs.backend.service.CustomerPersonalCenterService;
import com.hs.backend.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 个人中心服务实现类
 */
@Service
@Slf4j
public class CustomerPersonalCenterServiceImpl implements CustomerPersonalCenterService {

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;
    
    @Autowired
    private RedisUtils redisUtils;
    
    @Autowired
    private com.hs.backend.mapper.OrderMapper orderMapper;
    
    @Autowired
    private com.hs.backend.mapper.ReviewMapper reviewMapper;
    
    @Autowired
    private com.hs.backend.mapper.UserMapper userMapper;
    
    @Autowired
    private com.aliyun.oss.OSS ossClient;
    
    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    
    /**
     * 用户信息缓存的 key 前缀
     */
    private static final String USER_INFO_CACHE_KEY = "user:info:";
    
    /**
     * 用户地址缓存的 key 前缀
     */
    private static final String USER_ADDRESS_CACHE_KEY = "user:address:";
    
    /**
     * 个人中心统计信息缓存的 key 前缀
     */
    private static final String PERSONAL_CENTER_STATS_CACHE_KEY = "user:stats:";

    @Override
    public CustomerInfo getCustomerProfile(Long userId) {
        log.debug("========== [开始] 获取用户信息 userId={} ==========", userId);
        
        // 先从缓存获取
        String cacheKey = USER_INFO_CACHE_KEY + userId;
        CustomerInfo customerInfo = redisUtils.get(cacheKey, CustomerInfo.class);
        
        if (customerInfo != null) {
            log.debug("[缓存命中] 用户信息 userId={}", userId);
            return customerInfo;
        }
        
        log.debug("[缓存未命中] 查询数据库 userId={}", userId);
        
        // 查询客户信息（使用 user_id 查询，不是主键 id）
        customerInfo = customerInfoMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CustomerInfo>()
                .eq("user_id", userId)
        );
        log.debug("[数据库查询] selectOne(user_id={}) 结果：{}", userId, customerInfo == null ? "null(不存在)" : "存在");
        
        if (customerInfo == null) {
            log.warn("[初始化] 用户信息不存在，准备创建 userId={}", userId);
            // 如果不存在，创建一条默认记录
            customerInfo = new CustomerInfo();
            customerInfo.setUserId(userId);
            customerInfo.setUsername("");
            customerInfo.setRealName("");
            customerInfo.setGender(0);
            customerInfo.setEmail("");
            customerInfo.setBirthday(null);
            customerInfo.setAvatar("");
            customerInfo.setMemberLevel(0);
            customerInfo.setPoints(0);
            customerInfo.setTotalOrders(0);
            customerInfo.setCompletedOrders(0);
            customerInfo.setTotalSpent(java.math.BigDecimal.ZERO);
            
            log.debug("[插入前] 准备执行 insert，userId={}", userId);
            try {
                customerInfoMapper.insert(customerInfo);
                log.info("[初始化成功] 创建新的客户信息 userId={}", userId);
            } catch (Exception e) {
                log.error("[插入失败] userId={}, 错误：{}", userId, e.getMessage());
                throw e;
            }
            
            // 计算订单统计数据
            calculateAndSetOrderStats(customerInfo, userId);
            
            // 存入缓存
            try {
                redisUtils.set(cacheKey, customerInfo, 30, TimeUnit.MINUTES);
                log.debug("[缓存已设置] 用户信息 userId={}", userId);
            } catch (Exception e) {
                log.warn("Redis 设置缓存失败：{}", e.getMessage());
            }
        } else {
            log.debug("[已存在] 客户信息已存在 userId={}", userId);
            
            // 关联查询 User 表，获取手机号
            User user = userMapper.selectById(userId);
            if (user != null) {
                customerInfo.setPhone(user.getPhone());
                log.debug("[关联查询] 已从 User 表获取手机号 userId={}, phone={}", userId, user.getPhone());
            }
            
            // 头像处理：将相对路径转换为完整 URL（预签名 URL）
            String avatar = customerInfo.getAvatar();
            if (avatar != null && !avatar.isEmpty() && !avatar.startsWith("http")) {
                try {
                    // 生成预签名 URL（有效期 30 天 = 43200 分钟）
                    Date expiration = new Date(System.currentTimeMillis() + 43200L * 60 * 1000);
                    java.net.URL signedUrl = ossClient.generatePresignedUrl(
                        bucketName, 
                        avatar, 
                        expiration
                    );
                    customerInfo.setAvatar(signedUrl.toString());
                    log.debug("[头像处理] 生成预签名 URL: {}", signedUrl.toString());
                } catch (Exception e) {
                    log.warn("[头像处理] 生成预签名 URL 失败，使用普通 URL：{}", e.getMessage());
                    // 降级方案：使用普通 URL
                    String fullUrl = "https://" + bucketName + "." + endpoint + "/" + avatar;
                    customerInfo.setAvatar(fullUrl);
                }
            }
            
            // 计算订单统计数据（每次刷新缓存）
            calculateAndSetOrderStats(customerInfo, userId);
            
            // 存入缓存
            try {
                redisUtils.set(cacheKey, customerInfo, 30, TimeUnit.MINUTES);
                log.debug("[缓存已设置] 用户信息 userId={}", userId);
            } catch (Exception e) {
                log.warn("Redis 设置缓存失败：{}", e.getMessage());
            }
        }
        
        // 计算订单统计数据
        calculateAndSetOrderStats(customerInfo, userId);
        
        // 存入缓存（30 分钟）
        try {
            redisUtils.set(cacheKey, customerInfo, 30, TimeUnit.MINUTES);
            log.debug("[缓存已设置] 用户信息 userId={}", userId);
        } catch (Exception e) {
            log.warn("Redis 设置缓存失败：{}", e.getMessage());
        }
        
        log.debug("========== [结束] 返回用户信息 userId={} ==========", userId);
        
        return customerInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomerProfile(Long userId, CustomerInfo customerInfo) {
        // 设置用户 ID（防止被篡改）
        customerInfo.setUserId(userId);
        // 设置更新时间
        customerInfo.setUpdateTime(LocalDateTime.now());
        
        int rows = customerInfoMapper.updateById(customerInfo);
        if (rows == 0) {
            throw new BusinessException("更新用户信息失败");
        }
        
        // 更新后删除缓存（下次查询会重新计算统计数据）
        String cacheKey = USER_INFO_CACHE_KEY + userId;
        redisUtils.delete(cacheKey);
        log.debug("[缓存失效] 用户信息已更新 userId={}", userId);
    }

    @Override
    public List<UserAddress> getUserAddresses(Long userId) {
        // 先从缓存获取
        String cacheKey = USER_ADDRESS_CACHE_KEY + userId;
        List<UserAddress> addresses = redisUtils.get(cacheKey, List.class);
        
        if (addresses != null) {
            log.debug("[缓存命中] 用户地址 userId={}", userId);
            return addresses;
        }
        
        log.debug("[缓存未命中] 查询数据库 userId={}", userId);
        
        // 根据用户 ID 查询所有地址，按是否默认降序排序
        addresses = userAddressMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserAddress>()
                .eq("user_id", userId)
                .orderByDesc("is_default")
        );
        
        // 存入缓存（30 分钟）
        redisUtils.set(cacheKey, addresses, 30, TimeUnit.MINUTES);
        return addresses;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserAddress(Long userId, UserAddress address) {
        // 设置用户 ID
        address.setUserId(userId);
        
        // 如果是默认地址，先将其他地址设为非默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            setAllAddressesNonDefault(userId);
        }
        
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        
        int rows = userAddressMapper.insert(address);
        if (rows == 0) {
            throw new BusinessException("添加地址失败");
        }
        
        // 更新后删除地址缓存
        String cacheKey = USER_ADDRESS_CACHE_KEY + userId;
        redisUtils.delete(cacheKey);
        log.debug("[缓存失效] 用户地址已添加 userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserAddress(Long userId, UserAddress address) {
        // 验证地址归属
        UserAddress existingAddress = userAddressMapper.selectById(address.getId());
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在或无权限修改");
        }
        
        // 如果设置为默认地址，先将其他地址设为非默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            setAllAddressesNonDefault(userId);
        }
        
        address.setUpdateTime(LocalDateTime.now());
        
        int rows = userAddressMapper.updateById(address);
        if (rows == 0) {
            throw new BusinessException("更新地址失败");
        }
        
        // 更新后删除地址缓存
        String cacheKey = USER_ADDRESS_CACHE_KEY + userId;
        redisUtils.delete(cacheKey);
        log.debug("[缓存失效] 用户地址已更新 userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserAddress(Long userId, Long addressId) {
        // 验证地址归属
        UserAddress existingAddress = userAddressMapper.selectById(addressId);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在或无权限删除");
        }
        
        int rows = userAddressMapper.deleteById(addressId);
        if (rows == 0) {
            throw new BusinessException("删除地址失败");
        }
        
        // 更新后删除地址缓存
        String cacheKey = USER_ADDRESS_CACHE_KEY + userId;
        redisUtils.delete(cacheKey);
        log.debug("[缓存失效] 用户地址已删除 userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long userId, Long addressId) {
        // 验证地址归属
        UserAddress existingAddress = userAddressMapper.selectById(addressId);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在或无权限操作");
        }
        
        // 先将所有地址设为非默认
        setAllAddressesNonDefault(userId);
        
        // 再设置当前地址为默认
        UserAddress address = new UserAddress();
        address.setId(addressId);
        address.setIsDefault(1);
        address.setUpdateTime(LocalDateTime.now());
        
        int rows = userAddressMapper.updateById(address);
        if (rows == 0) {
            throw new BusinessException("设置默认地址失败");
        }
        
        // 更新后删除地址缓存
        String cacheKey = USER_ADDRESS_CACHE_KEY + userId;
        redisUtils.delete(cacheKey);
        log.debug("[缓存失效] 默认地址已设置 userId={}", userId);
    }

    /**
     * 将所有地址设为非默认
     */
    private void setAllAddressesNonDefault(Long userId) {
        List<UserAddress> addresses = userAddressMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserAddress>()
                .eq("user_id", userId)
                .eq("is_default", 1)
        );
        
        for (UserAddress address : addresses) {
            address.setIsDefault(0);
            address.setUpdateTime(LocalDateTime.now());
            userAddressMapper.updateById(address);
        }
    }

    /**
     * 计算并设置订单统计数据
     */
    private void calculateAndSetOrderStats(CustomerInfo customerInfo, Long userId) {
        try {
            // 定义订单状态分类
            // 已完成订单：服务完成 (4) + 已退款 (7)
            java.util.List<Integer> completedStatuses = java.util.Arrays.asList(4, 7);
            
            // 查询总订单数
            Long totalOrders = orderMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                    .eq("user_id", userId)
            );
            
            // 查询已完成订单数
            Long completedOrders = orderMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                    .eq("user_id", userId)
                    .in("status", completedStatuses)
            );
            
            // 设置到对象中
            customerInfo.setTotalOrders(totalOrders.intValue());
            customerInfo.setCompletedOrders(completedOrders.intValue());
            
            log.debug("订单统计计算完成 userId={}, totalOrders={}, completedOrders={}", 
                userId, totalOrders, completedOrders);
        } catch (Exception e) {
            log.warn("计算订单统计失败：{}", e.getMessage());
            // 设置默认值
            if (customerInfo.getTotalOrders() == null) customerInfo.setTotalOrders(0);
            if (customerInfo.getCompletedOrders() == null) customerInfo.setCompletedOrders(0);
        }
    }

    @Override
    public java.util.Map<String, Object> getPersonalCenterStats(Long userId) {
        // 直接从缓存获取用户信息（包含统计数据）
        String infoCacheKey = USER_INFO_CACHE_KEY + userId;
        CustomerInfo customerInfo = null;
        
        try {
            customerInfo = redisUtils.get(infoCacheKey, CustomerInfo.class);
        } catch (Exception e) {
            log.warn("Redis 连接失败，跳过缓存直接查询数据库：{}", e.getMessage());
        }
        
        // 如果缓存没有，则调用 getCustomerProfile 会创建并缓存
        if (customerInfo == null) {
            customerInfo = getCustomerProfile(userId);
        }
        
        // 构建返回结果
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalOrders", customerInfo.getTotalOrders() != null ? customerInfo.getTotalOrders() : 0);
        stats.put("completedOrders", customerInfo.getCompletedOrders() != null ? customerInfo.getCompletedOrders() : 0);
        stats.put("ongoingOrders", (customerInfo.getTotalOrders() != null ? customerInfo.getTotalOrders() : 0) - 
                                   (customerInfo.getCompletedOrders() != null ? customerInfo.getCompletedOrders() : 0));
        stats.put("totalSpent", customerInfo.getTotalSpent() != null ? customerInfo.getTotalSpent() : 0.0);
        // TODO: 收藏和优惠券功能待实现
        stats.put("favoritesCount", 0);
        stats.put("couponsCount", 0);
        
        return stats;
    }
}
