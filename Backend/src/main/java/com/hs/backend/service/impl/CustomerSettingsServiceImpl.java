package com.hs.backend.service.impl;

import com.aliyun.oss.OSS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.dto.request.CustomerAddressRequest;
import com.hs.backend.dto.request.SettingsProfileUpdateRequest;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.User;
import com.hs.backend.entity.UserAddress;
import com.hs.backend.mapper.CustomerInfoMapper;
import com.hs.backend.mapper.UserAddressMapper;
import com.hs.backend.mapper.UserMapper;
import com.hs.backend.service.CustomerSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 设置服务实现
 */
@Slf4j
@Service
public class CustomerSettingsServiceImpl implements CustomerSettingsService {

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private OSS ossClient;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Override
    public CustomerInfo getProfile(Long userId) {
        QueryWrapper<CustomerInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        CustomerInfo customerInfo = customerInfoMapper.selectOne(queryWrapper);

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

        return customerInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long userId, SettingsProfileUpdateRequest request) {
        QueryWrapper<CustomerInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        CustomerInfo customerInfo = customerInfoMapper.selectOne(queryWrapper);

        if (customerInfo == null) {
            throw new BusinessException("用户信息不存在");
        }

        customerInfo.setUsername(request.getUsername());
        customerInfo.setRealName(request.getRealName());
        customerInfo.setGender(request.getGender());
        customerInfo.setEmail(request.getEmail());
        customerInfo.setBirthday(request.getBirthday());

        if (request.getAvatar() != null) {
            String avatar = request.getAvatar();
            if (avatar.startsWith("http://") || avatar.startsWith("https://")) {
                try {
                    java.net.URI uri = new java.net.URI(avatar);
                    String path = uri.getPath();
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

        customerInfo.setUpdateTime(LocalDateTime.now());

        int rows = customerInfoMapper.updateById(customerInfo);
        if (rows == 0) {
            throw new BusinessException("更新用户信息失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindPhone(Long userId, String newPhone, String currentPassword) {
        log.info("开始绑定手机号 userId={}, newPhone={}", userId, newPhone);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException("当前密码错误");
        }

        if (user.getPhone() != null && user.getPhone().equals(newPhone)) {
            throw new BusinessException("新手机号与当前手机号相同");
        }

        QueryWrapper<User> phoneQueryWrapper = new QueryWrapper<>();
        phoneQueryWrapper.eq("phone", newPhone);
        User existingUser = userMapper.selectOne(phoneQueryWrapper);

        if (existingUser != null && !existingUser.getId().equals(userId)) {
            throw new BusinessException("该手机号已被其他用户使用");
        }

        user.setPhone(newPhone);
        user.setUpdateTime(LocalDateTime.now());

        int rows = userMapper.updateById(user);
        if (rows == 0) {
            throw new BusinessException("更新手机号失败");
        }

        log.info("手机号绑定成功 userId={}, newPhone={}", userId, newPhone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("开始修改密码 userId={}", userId);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }

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
        QueryWrapper<UserAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("is_default")
                .orderByDesc("create_time");
        return userAddressMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAddress(Long userId, CustomerAddressRequest request) {
        log.info("开始添加地址 userId={}", userId);

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
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long userId, CustomerAddressRequest request) {
        if (request.getId() == null) {
            throw new BusinessException("地址ID不能为空");
        }

        log.info("开始更新地址 userId={}, addressId={}", userId, request.getId());

        UserAddress existingAddress = userAddressMapper.selectById(request.getId());
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在或无权操作");
        }

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
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long userId, Long addressId) {
        log.info("开始删除地址 userId={}, addressId={}", userId, addressId);

        UserAddress existingAddress = userAddressMapper.selectById(addressId);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在或无权操作");
        }

        int rows = userAddressMapper.deleteById(addressId);
        if (rows == 0) {
            throw new BusinessException("删除地址失败");
        }

        log.info("地址删除成功 addressId={}", addressId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long userId, Long addressId) {
        log.info("开始设置默认地址 userId={}, addressId={}", userId, addressId);

        UserAddress existingAddress = userAddressMapper.selectById(addressId);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在或无权操作");
        }

        cancelDefaultAddress(userId);

        existingAddress.setIsDefault(1);
        int rows = userAddressMapper.updateById(existingAddress);
        if (rows == 0) {
            throw new BusinessException("设置默认地址失败");
        }

        log.info("默认地址设置成功 addressId={}", addressId);
    }

    /**
     * 取消所有默认地址。
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
     * 检查并升级会员等级（升级时扣除相应积分）。
     */
    public void checkAndUpdateMemberLevel(Long userId) {
        QueryWrapper<CustomerInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        CustomerInfo customerInfo = customerInfoMapper.selectOne(queryWrapper);

        if (customerInfo == null) {
            log.warn("用户信息不存在 userId={}", userId);
            return;
        }

        Integer currentPoints = customerInfo.getPoints();
        Integer currentLevel = customerInfo.getMemberLevel();

        java.util.Map<Integer, Integer> levelConfig = new java.util.HashMap<>();
        levelConfig.put(1, 1000);
        levelConfig.put(2, 3000);
        levelConfig.put(3, 5000);
        levelConfig.put(4, 10000);

        Integer newLevel = currentLevel;

        for (int level = currentLevel + 1; level <= 4; level++) {
            Integer requiredPoints = levelConfig.get(level);
            if (requiredPoints != null && currentPoints >= requiredPoints) {
                newLevel = level;
            } else {
                break;
            }
        }

        if (newLevel > currentLevel) {
            Integer pointsToDeduct = levelConfig.get(newLevel);
            Integer remainingPoints = currentPoints - pointsToDeduct;

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
        }
    }
}
