package com.hs.backend.service.impl;

import com.aliyun.oss.OSS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hs.backend.dto.request.CustomerAddressRequest;
import com.hs.backend.dto.request.SettingsProfileUpdateRequest;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.entity.Order;
import com.hs.backend.entity.User;
import com.hs.backend.entity.UserAddress;
import com.hs.backend.mapper.CustomerInfoMapper;
import com.hs.backend.mapper.UserAddressMapper;
import com.hs.backend.mapper.UserMapper;
import com.hs.backend.service.CustomerSettingsService;
import com.hs.backend.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 设置服务实现类
 */
@Slf4j
@Service
public class CustomerSettingsServiceImpl implements CustomerSettingsService {

    @Autowired
    private CustomerInfoMapper customerInfoMapper;
    
    @Autowired
    private RedisUtils redisUtils;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserAddressMapper userAddressMapper;
    
    @Autowired
    private OSS ossClient;
    
    @Autowired
    private com.hs.backend.mapper.OrderMapper ordersMapper;
    
    @Autowired
    private com.hs.backend.mapper.ReviewMapper reviewMapper;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    
    @Value("${spring.redis.cache.user-info-ttl:30}")
    private long userInfoTtl;
    
    @Value("${spring.redis.cache.user-address-ttl:30}")
    private long userAddressTtl;
    
    /**
     * 用户信息缓存的 key 前缀
     */
    private static final String USER_INFO_CACHE_KEY = "user:info:";
    
    /**
     * 用户地址列表缓存的 key 前缀
     */
    private static final String USER_ADDRESS_CACHE_KEY = "user:address:";

    @Override
    public CustomerInfo getProfile(Long userId) {
        // 先从 Redis 缓存中获取
        String cacheKey = USER_INFO_CACHE_KEY + userId;
        CustomerInfo customerInfo = null;
        
        try {
            customerInfo = redisUtils.get(cacheKey, CustomerInfo.class);
        } catch (Exception e) {
            log.warn("Redis 连接失败，跳过缓存直接查询数据库：{}", e.getMessage());
        }
        
        if (customerInfo != null) {
            log.debug("[缓存命中] 用户信息 userId={}", userId);
            return customerInfo;
        }
        
        log.debug("[缓存未命中] 查询数据库 userId={}", userId);
        
        // 缓存未命中，从数据库查询
        QueryWrapper<CustomerInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        customerInfo = customerInfoMapper.selectOne(queryWrapper);
        
        if (customerInfo == null) {
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
            customerInfoMapper.insert(customerInfo);
            
            // 计算订单统计数据
            calculateAndSetOrderStats(customerInfo, userId);
            
            // 缓存新创建的记录
            try {
                redisUtils.set(cacheKey, customerInfo, userInfoTtl, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.warn("Redis 设置缓存失败：{}", e.getMessage());
            }
        } else {
            // 关联查询 User 表，获取手机号
            User user = userMapper.selectById(userId);
            if (user != null) {
                customerInfo.setPhone(user.getPhone());
            }
            
            // 计算订单统计数据（每次刷新缓存）
            calculateAndSetOrderStats(customerInfo, userId);
            
            // 头像处理：将相对路径转换为完整 URL
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
                    log.debug("生成头像预签名 URL: {}", signedUrl.toString());
                } catch (Exception e) {
                    log.warn("生成预签名 URL 失败，使用普通 URL：{}", e.getMessage());
                    // 降级方案：使用普通 URL
                    String fullUrl = "https://" + bucketName + "." + endpoint + "/" + avatar;
                    customerInfo.setAvatar(fullUrl);
                }
            }
            
            // 存入缓存（从配置文件读取时间）
            try {
                redisUtils.set(cacheKey, customerInfo, userInfoTtl, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.warn("Redis 设置缓存失败：{}", e.getMessage());
            }
        }
        
        return customerInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long userId, SettingsProfileUpdateRequest request) {
        // 根据 user_id 查询客户信息（不是 id）
        QueryWrapper<CustomerInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        CustomerInfo customerInfo = customerInfoMapper.selectOne(queryWrapper);
        
        if (customerInfo == null) {
            throw new BusinessException("用户信息不存在");
        }

        // 更新字段
        customerInfo.setUsername(request.getUsername());
        customerInfo.setRealName(request.getRealName());
        customerInfo.setGender(request.getGender());
        customerInfo.setEmail(request.getEmail());
        customerInfo.setBirthday(request.getBirthday());
        
        // 头像单独处理（可能为空）
        if (request.getAvatar() != null) {
            String avatar = request.getAvatar();
            // 如果是完整 URL，提取相对路径
            if (avatar.startsWith("http://") || avatar.startsWith("https://")) {
                // 从完整 URL 中提取相对路径
                // 例如：https://lihuahua-cookhome.oss-cn-hangzhou.aliyuncs.com/avatar/7/xxx.jpg
                // 提取为：avatar/7/xxx.jpg
                try {
                    java.net.URI uri = new java.net.URI(avatar);
                    String path = uri.getPath();
                    // 移除开头的 '/'
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    avatar = path;
                } catch (Exception e) {
                    log.warn("解析头像 URL 失败，使用原始值：{}", avatar);
                }
            }
            customerInfo.setAvatar(avatar);
        }

        // 设置更新时间（MyBatis-Plus 会自动处理，但这里显式设置确保一致性）
        customerInfo.setUpdateTime(LocalDateTime.now());

        int rows = customerInfoMapper.updateById(customerInfo);
        if (rows == 0) {
            throw new BusinessException("更新用户信息失败");
        }
        
        // 更新后删除缓存，保证数据一致性
        String cacheKey = USER_INFO_CACHE_KEY + userId;
        try {
            redisUtils.delete(cacheKey);
            log.debug("[缓存失效] 用户信息已更新 userId={}, cacheKey={}", userId, cacheKey);
        } catch (Exception e) {
            log.warn("Redis 删除缓存失败：{}", e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindPhone(Long userId, String newPhone, String currentPassword) {
        log.info("开始绑定手机号 userId={}, newPhone={}", userId, newPhone);
        
        // 1. 验证当前密码是否正确
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException("当前密码错误");
        }
        
        // 2. 检查新手机号是否与当前手机号相同
        if (user.getPhone() != null && user.getPhone().equals(newPhone)) {
            throw new BusinessException("新手机号与当前手机号相同");
        }
        
        // 3. 验证手机号是否已被其他用户使用
        QueryWrapper<User> phoneQueryWrapper = new QueryWrapper<>();
        phoneQueryWrapper.eq("phone", newPhone);
        User existingUser = userMapper.selectOne(phoneQueryWrapper);
        
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            throw new BusinessException("该手机号已被其他用户使用");
        }
        
        // 4. 更新用户手机号
        user.setPhone(newPhone);
        user.setUpdateTime(LocalDateTime.now());
        
        int rows = userMapper.updateById(user);
        if (rows == 0) {
            throw new BusinessException("更新手机号失败");
        }
        
        log.info("手机号绑定成功 userId={}, newPhone={}", userId, newPhone);
        
        // 更新后删除缓存，保证数据一致性
        String cacheKey = USER_INFO_CACHE_KEY + userId;
        try {
            redisUtils.delete(cacheKey);
            log.debug("[缓存失效] 用户信息已更新 userId={}, cacheKey={}", userId, cacheKey);
        } catch (Exception e) {
            log.warn("Redis 删除缓存失败：{}", e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("开始修改密码 userId={}", userId);
        
        // 1. 验证原密码是否正确
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        
        // 2. 检查新密码是否与原密码相同
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }
        
        // 3. 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        
        int rows = userMapper.updateById(user);
        if (rows == 0) {
            throw new BusinessException("修改密码失败");
        }
        
        log.info("密码修改成功 userId={}", userId);
    }

    @Override
    public List<UserAddress> getAddressList(Long userId) {
        // 先从 Redis 缓存中获取
        String cacheKey = USER_ADDRESS_CACHE_KEY + userId;
        List<UserAddress> addressList = null;
        
        try {
            addressList = redisUtils.get(cacheKey, List.class);
            if (addressList != null) {
                log.debug("[缓存命中] 用户地址列表 userId={}", userId);
                return addressList;
            }
        } catch (Exception e) {
            log.warn("Redis 连接失败，跳过缓存直接查询数据库：{}", e.getMessage());
        }
        
        log.debug("[缓存未命中] 查询数据库 userId={}", userId);
        
        // 缓存未命中，从数据库查询
        QueryWrapper<UserAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                    .orderByDesc("is_default")
                    .orderByDesc("create_time");
        addressList = userAddressMapper.selectList(queryWrapper);
        
        // 存入缓存（从配置文件读取时间）
        try {
            if (addressList != null && !addressList.isEmpty()) {
                redisUtils.set(cacheKey, addressList, userAddressTtl, java.util.concurrent.TimeUnit.MINUTES);
                log.debug("[缓存设置] 用户地址列表已缓存 userId={}", userId);
            }
        } catch (Exception e) {
            log.warn("Redis 设置缓存失败：{}", e.getMessage());
        }
        
        return addressList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAddress(Long userId, CustomerAddressRequest request) {
        log.info("开始添加地址 userId={}", userId);
        
        // 如果设置为默认地址，先取消其他默认地址
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            cancelDefaultAddress(userId);
        }
        
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        userAddress.setReceiver(request.getReceiver());
        userAddress.setPhone(request.getPhone());
        userAddress.setProvince(request.getProvince());
        userAddress.setCity(request.getCity());
        userAddress.setDistrict(request.getDistrict());
        userAddress.setDetailAddress(request.getDetailAddress());
        userAddress.setLatitude(request.getLatitude());
        userAddress.setLongitude(request.getLongitude());
        userAddress.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : 0);
        
        int rows = userAddressMapper.insert(userAddress);
        if (rows == 0) {
            throw new BusinessException("添加地址失败");
        }
        
        log.info("地址添加成功 addressId={}", userAddress.getId());
        
        // 删除缓存，保证数据一致性
        deleteAddressCache(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long userId, CustomerAddressRequest request) {
        if (request.getId() == null) {
            throw new BusinessException("地址 ID 不能为空");
        }
        
        log.info("开始更新地址 userId={}, addressId={}", userId, request.getId());
        
        // 验证地址是否属于当前用户
        UserAddress existingAddress = userAddressMapper.selectById(request.getId());
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在或无权操作");
        }
        
        // 如果设置为默认地址，先取消其他默认地址
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            cancelDefaultAddress(userId);
        }
        
        existingAddress.setReceiver(request.getReceiver());
        existingAddress.setPhone(request.getPhone());
        existingAddress.setProvince(request.getProvince());
        existingAddress.setCity(request.getCity());
        existingAddress.setDistrict(request.getDistrict());
        existingAddress.setDetailAddress(request.getDetailAddress());
        existingAddress.setLatitude(request.getLatitude());
        existingAddress.setLongitude(request.getLongitude());
        existingAddress.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : 0);
        
        int rows = userAddressMapper.updateById(existingAddress);
        if (rows == 0) {
            throw new BusinessException("更新地址失败");
        }
        
        log.info("地址更新成功 addressId={}", request.getId());
        
        // 删除缓存，保证数据一致性
        deleteAddressCache(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long userId, Long addressId) {
        log.info("开始删除地址 userId={}, addressId={}", userId, addressId);
        
        // 验证地址是否属于当前用户
        UserAddress existingAddress = userAddressMapper.selectById(addressId);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在或无权操作");
        }
        
        int rows = userAddressMapper.deleteById(addressId);
        if (rows == 0) {
            throw new BusinessException("删除地址失败");
        }
        
        log.info("地址删除成功 addressId={}", addressId);
        
        // 删除缓存，保证数据一致性
        deleteAddressCache(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long userId, Long addressId) {
        log.info("开始设置默认地址 userId={}, addressId={}", userId, addressId);
        
        // 验证地址是否属于当前用户
        UserAddress existingAddress = userAddressMapper.selectById(addressId);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在或无权操作");
        }
        
        // 先取消所有默认地址
        cancelDefaultAddress(userId);
        
        // 设置新的默认地址
        existingAddress.setIsDefault(1);
        int rows = userAddressMapper.updateById(existingAddress);
        if (rows == 0) {
            throw new BusinessException("设置默认地址失败");
        }
        
        log.info("默认地址设置成功 addressId={}", addressId);
        
        // 删除缓存，保证数据一致性
        deleteAddressCache(userId);
    }

    /**
     * 取消所有默认地址
     */
    private void cancelDefaultAddress(Long userId) {
        QueryWrapper<UserAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("is_default", 1);
        List<UserAddress> defaultAddresses = userAddressMapper.selectList(queryWrapper);
        
        for (UserAddress address : defaultAddresses) {
            address.setIsDefault(0);
            userAddressMapper.updateById(address);
        }
    }

    /**
     * 删除用户地址缓存
     */
    private void deleteAddressCache(Long userId) {
        String cacheKey = USER_ADDRESS_CACHE_KEY + userId;
        try {
            redisUtils.delete(cacheKey);
            log.debug("[缓存失效] 用户地址列表已删除 userId={}, cacheKey={}", userId, cacheKey);
        } catch (Exception e) {
            log.warn("Redis 删除缓存失败：{}", e.getMessage());
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
            Long totalOrders = ordersMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                    .eq("user_id", userId)
            );
            
            // 查询已完成订单数
            Long completedOrders = ordersMapper.selectCount(
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
    
    /**
     * 检查并升级会员等级（升级时扣除相应积分）
     */
    public void checkAndUpdateMemberLevel(Long userId) {
        // 根据 user_id 查询（不是 id）
        QueryWrapper<CustomerInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        CustomerInfo customerInfo = customerInfoMapper.selectOne(queryWrapper);
        
        if (customerInfo == null) {
            log.warn("用户信息不存在 userId={}", userId);
            return;
        }
        
        Integer currentPoints = customerInfo.getPoints();
        Integer currentLevel = customerInfo.getMemberLevel();
        
        // 会员等级配置：等级 -> 所需积分
        java.util.Map<Integer, Integer> levelConfig = new java.util.HashMap<>();
        levelConfig.put(1, 1000);  // 白银会员：1000 积分
        levelConfig.put(2, 3000);  // 黄金会员：3000 积分
        levelConfig.put(3, 5000);  // 铂金会员：5000 积分
        levelConfig.put(4, 10000); // 钻石会员：10000 积分
        
        Integer newLevel = currentLevel;
        
        // 检查是否可以升级到更高等级
        for (int level = currentLevel + 1; level <= 4; level++) {
            Integer requiredPoints = levelConfig.get(level);
            if (requiredPoints != null && currentPoints >= requiredPoints) {
                newLevel = level;
            } else {
                break;
            }
        }
        
        // 如果等级提升，更新等级并扣除积分
        if (newLevel > currentLevel) {
            Integer pointsToDeduct = levelConfig.get(newLevel);
            Integer remainingPoints = currentPoints - pointsToDeduct;
            
            // 更新会员等级和剩余积分
            customerInfo.setMemberLevel(newLevel);
            customerInfo.setPoints(Math.max(remainingPoints, 0));
            customerInfo.setUpdateTime(LocalDateTime.now());
            
            int rows = customerInfoMapper.updateById(customerInfo);
            if (rows == 0) {
                log.error("会员升级更新失败 userId={}", userId);
                throw new BusinessException("会员升级更新失败");
            }
            
            log.info("会员升级成功 userId={}, oldLevel={}, newLevel={}, usedPoints={}, remainingPoints={}",
                userId, currentLevel, newLevel, pointsToDeduct, remainingPoints);
            
            // 清除缓存
            String cacheKey = USER_INFO_CACHE_KEY + userId;
            redisUtils.delete(cacheKey);
        }
    }
}
