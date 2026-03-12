package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hs.backend.dto.request.SettingsProfileUpdateRequest;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.mapper.CustomerInfoMapper;
import com.hs.backend.service.CustomerSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 设置服务实现类
 */
@Slf4j
@Service
public class CustomerSettingsServiceImpl implements CustomerSettingsService {

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Override
    public CustomerInfo getProfile(Long userId) {
        // 根据 user_id 查询客户信息（不是 id）
        QueryWrapper<CustomerInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        CustomerInfo customerInfo = customerInfoMapper.selectOne(queryWrapper);
        
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
        } else {
            // 将相对路径转换为完整 URL
            String avatar = customerInfo.getAvatar();
            if (avatar != null && !avatar.isEmpty() && !avatar.startsWith("http")) {
                // 拼接完整 URL: https://{bucket}.{endpoint}/{avatar}
                String fullUrl = "https://" + bucketName + "." + endpoint + "/" + avatar;
                customerInfo.setAvatar(fullUrl);
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
    }
}
