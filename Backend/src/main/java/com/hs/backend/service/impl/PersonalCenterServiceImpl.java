package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.UserAddress;
import com.hs.backend.mapper.CustomerInfoMapper;
import com.hs.backend.mapper.UserAddressMapper;
import com.hs.backend.service.PersonalCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 个人中心服务实现类
 */
@Service
public class PersonalCenterServiceImpl implements PersonalCenterService {

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public CustomerInfo getCustomerProfile(Long userId) {
        // 查询客户信息
        CustomerInfo customerInfo = customerInfoMapper.selectById(userId);
        if (customerInfo == null) {
            throw new BusinessException("用户信息不存在");
        }
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
    }

    @Override
    public List<UserAddress> getUserAddresses(Long userId) {
        // 根据用户 ID 查询所有地址，按是否默认降序排序
        return userAddressMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserAddress>()
                .eq("user_id", userId)
                .orderByDesc("is_default")
        );
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
