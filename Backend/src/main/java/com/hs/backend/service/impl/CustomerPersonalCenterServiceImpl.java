package com.hs.backend.service.impl;

import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.Order;
import com.hs.backend.entity.User;
import com.hs.backend.entity.UserAddress;
import com.hs.backend.mapper.CustomerInfoMapper;
import com.hs.backend.mapper.UserAddressMapper;
import com.hs.backend.service.CustomerPersonalCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 个人中心服务实现
 */
@Service
@Slf4j
public class CustomerPersonalCenterServiceImpl implements CustomerPersonalCenterService {

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

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

    @Override
    public CustomerInfo getCustomerProfile(Long userId) {
        CustomerInfo customerInfo = customerInfoMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CustomerInfo>()
                        .eq("user_id", userId)
        );

        if (customerInfo == null) {
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
        } else {
            User user = userMapper.selectById(userId);
            if (user != null) {
                customerInfo.setPhone(user.getPhone());
            }

            String avatar = customerInfo.getAvatar();
            if (avatar != null && !avatar.isEmpty() && !avatar.startsWith("http")) {
                try {
                    Date expiration = new Date(System.currentTimeMillis() + 43200L * 60 * 1000);
                    java.net.URL signedUrl = ossClient.generatePresignedUrl(
                            bucketName,
                            avatar,
                            expiration
                    );
                    customerInfo.setAvatar(signedUrl.toString());
                } catch (Exception e) {
                    log.warn("生成头像签名 URL 失败，使用普通 URL：{}", e.getMessage());
                    String fullUrl = "https://" + bucketName + "." + endpoint + "/" + avatar;
                    customerInfo.setAvatar(fullUrl);
                }
            }
        }

        calculateAndSetOrderStats(customerInfo, userId);
        return customerInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomerProfile(Long userId, CustomerInfo customerInfo) {
        customerInfo.setUserId(userId);
        customerInfo.setUpdateTime(LocalDateTime.now());

        int rows = customerInfoMapper.updateById(customerInfo);
        if (rows == 0) {
            throw new BusinessException("更新用户信息失败");
        }
    }

    @Override
    public List<UserAddress> getUserAddresses(Long userId) {
        return userAddressMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserAddress>()
                        .eq("user_id", userId)
                        .orderByDesc("is_default")
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserAddress(Long userId, UserAddress address) {
        address.setUserId(userId);

        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            setAllAddressesNonDefault(userId);
        }

        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());

        int rows = userAddressMapper.insert(address);
        if (rows == 0) {
            throw new BusinessException("添加地址失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserAddress(Long userId, UserAddress address) {
        UserAddress existingAddress = userAddressMapper.selectById(address.getId());
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在或无权限修改");
        }

        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            setAllAddressesNonDefault(userId);
        }

        address.setUpdateTime(LocalDateTime.now());

        int rows = userAddressMapper.updateById(address);
        if (rows == 0) {
            throw new BusinessException("更新地址失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserAddress(Long userId, Long addressId) {
        UserAddress existingAddress = userAddressMapper.selectById(addressId);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在或无权限删除");
        }

        int rows = userAddressMapper.deleteById(addressId);
        if (rows == 0) {
            throw new BusinessException("删除地址失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long userId, Long addressId) {
        UserAddress existingAddress = userAddressMapper.selectById(addressId);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在或无权限操作");
        }

        setAllAddressesNonDefault(userId);

        UserAddress address = new UserAddress();
        address.setId(addressId);
        address.setIsDefault(1);
        address.setUpdateTime(LocalDateTime.now());

        int rows = userAddressMapper.updateById(address);
        if (rows == 0) {
            throw new BusinessException("设置默认地址失败");
        }
    }

    /**
     * 将所有地址设为非默认。
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
     * 计算并设置订单统计。
     */
    private void calculateAndSetOrderStats(CustomerInfo customerInfo, Long userId) {
        try {
            java.util.List<Integer> completedStatuses = java.util.Arrays.asList(4, 7);

            Long totalOrders = orderMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                            .eq("user_id", userId)
            );

            Long completedOrders = orderMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                            .eq("user_id", userId)
                            .in("status", completedStatuses)
            );

            customerInfo.setTotalOrders(totalOrders.intValue());
            customerInfo.setCompletedOrders(completedOrders.intValue());
        } catch (Exception e) {
            log.warn("计算订单统计失败：{}", e.getMessage());
            if (customerInfo.getTotalOrders() == null) {
                customerInfo.setTotalOrders(0);
            }
            if (customerInfo.getCompletedOrders() == null) {
                customerInfo.setCompletedOrders(0);
            }
        }
    }

    @Override
    public java.util.Map<String, Object> getPersonalCenterStats(Long userId) {
        CustomerInfo customerInfo = getCustomerProfile(userId);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalOrders", customerInfo.getTotalOrders() != null ? customerInfo.getTotalOrders() : 0);
        stats.put("completedOrders", customerInfo.getCompletedOrders() != null ? customerInfo.getCompletedOrders() : 0);
        stats.put("ongoingOrders", (customerInfo.getTotalOrders() != null ? customerInfo.getTotalOrders() : 0)
                - (customerInfo.getCompletedOrders() != null ? customerInfo.getCompletedOrders() : 0));
        stats.put("totalSpent", customerInfo.getTotalSpent() != null ? customerInfo.getTotalSpent() : 0.0);
        stats.put("favoritesCount", 0);
        stats.put("couponsCount", 0);

        return stats;
    }
}
