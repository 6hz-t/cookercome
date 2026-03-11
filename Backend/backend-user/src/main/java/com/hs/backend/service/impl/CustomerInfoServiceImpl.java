package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.mapper.CustomerInfoMapper;
import com.hs.backend.service.CustomerInfoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 客户信息服务实现类
 */
@Service
public class CustomerInfoServiceImpl extends ServiceImpl<CustomerInfoMapper, CustomerInfo> implements CustomerInfoService {

    // 默认头像地址（OSS 相对路径）
    private static final String DEFAULT_AVATAR = "/avatars/default-avatar.png";

    @Override
    public CustomerInfo createDefaultCustomerInfo(Long userId, String username) {
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setUserId(userId);
        customerInfo.setUsername(username);
        customerInfo.setAvatar(DEFAULT_AVATAR);
        customerInfo.setGender(0);  // 默认性别：未知
        customerInfo.setCreateTime(LocalDateTime.now());
        customerInfo.setUpdateTime(LocalDateTime.now());
        // create_time 和 update_time 会自动填充
        
        save(customerInfo);
        return customerInfo;
    }
}
