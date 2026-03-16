package com.hs.backend.service.impl;

import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.UserAddress;
import com.hs.backend.mapper.CustomerInfoMapper;
import com.hs.backend.mapper.UserAddressMapper;
import com.hs.backend.service.CustomerPersonalCenterService;
import com.hs.backend.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 个人中心服务实现类
 */
@Service
@Slf4j
public class PersonalCenterServiceImpl implements CustomerPersonalCenterService {

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;
    
    @Autowired
    private RedisUtils redisUtils;
    
    /**
     * 用户信息缓存的 key 前缀
     */
    private static final String USER_INFO_CACHE_KEY = "user:info:";
    
    /**
     * 用户地址缓存的 key 前缀
     */
    private static final String USER_ADDRESS_CACHE_KEY = "user:address:";

    @Override
    public CustomerInfo getCustomerProfile(Long userId) {
        // 先从缓存获取
        String cacheKey = USER_INFO_CACHE_KEY + userId;
        CustomerInfo customerInfo = redisUtils.get(cacheKey, CustomerInfo.class);
        
        if (customerInfo != null) {
            log.debug("[缓存命中] 用户信息 userId={}", userId);
            return customerInfo;
        }
        
        log.debug("[缓存未命中] 查询数据库 userId={}", userId);
        
        // 查询客户信息
        customerInfo = customerInfoMapper.selectById(userId);
        if (customerInfo == null) {
            throw new BusinessException("用户信息不存在");
        }
        
        // 存入缓存（10 分钟）
        redisUtils.set(cacheKey, customerInfo, 10, TimeUnit.MINUTES);
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
        
        // 更新后删除缓存
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
}
