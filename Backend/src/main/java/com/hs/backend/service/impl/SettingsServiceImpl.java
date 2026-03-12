package com.hs.backend.service.impl;

import com.hs.backend.dto.request.SettingsProfileUpdateRequest;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.mapper.CustomerInfoMapper;
import com.hs.backend.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 设置服务实现类
 */
@Service
public class SettingsServiceImpl implements SettingsService {

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Override
    public CustomerInfo getProfile(Long userId) {
        // 查询客户信息
        CustomerInfo customerInfo = customerInfoMapper.selectById(userId);
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
            customerInfoMapper.insert(customerInfo);
        }
        return customerInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long userId, SettingsProfileUpdateRequest request) {
        // 查询客户信息
        CustomerInfo customerInfo = customerInfoMapper.selectById(userId);
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
            customerInfo.setAvatar(request.getAvatar());
        }

        // 设置更新时间（MyBatis-Plus 会自动处理，但这里显式设置确保一致性）
        customerInfo.setUpdateTime(LocalDateTime.now());

        int rows = customerInfoMapper.updateById(customerInfo);
        if (rows == 0) {
            throw new BusinessException("更新用户信息失败");
        }
    }
}
