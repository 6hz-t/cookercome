package com.hs.backend.service.impl;

import  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.common.config.OssConfig;
import com.hs.backend.dto.response.AuthResponse;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.User;
import com.hs.backend.mapper.UserMapper;
import com.hs.backend.security.JwtTokenProvider;
import com.hs.backend.service.CustomerInfoService;
import com.hs.backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomerInfoService customerInfoService;
    private final OssConfig ossConfig;

    public UserServiceImpl(PasswordEncoder passwordEncoder, 
                          JwtTokenProvider jwtTokenProvider,
                          CustomerInfoService customerInfoService,
                          OssConfig ossConfig) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.customerInfoService = customerInfoService;
        this.ossConfig = ossConfig;
    }

    @Override
    public User register(String phone, String password, Integer role) {
        // 检查手机号是否存在
        User existingPhone = getByPhone(phone);
        if (existingPhone != null) {
            throw new BusinessException("手机号已被注册");
        }

        // 创建用户
        User user = new User();
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role != null ? role : 0);  // 默认为客户（role=0）
        // 手动设置时间（确保不会为 NULL）
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        save(user);
        
        // 创建默认客户信息
        String username = "新用户" + user.getId();
        customerInfoService.createDefaultCustomerInfo(user.getId(), username);
        
        return user;
    }

    @Override
    public AuthResponse login(String phone, String password) {
        // 通过手机号查找用户
        User user = getByPhone(phone);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 生成双 Token
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // 构建响应
        AuthResponse.AuthResponseBuilder responseBuilder = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .userInfo(AuthResponse.UserInfoVO.builder()
                        .id(user.getId())
                        .phone(user.getPhone())
                        .role(user.getRole())
                        .build());
        
        // 如果是客户，查询并返回客户详细信息
        if (user.getRole() == 0) {
            CustomerInfo customerInfo = getCustomerInfoByUserId(user.getId());
            if (customerInfo != null) {
                responseBuilder.customerInfo(toCustomerInfoVO(customerInfo));
            }
        }
        
        return responseBuilder.build();
    }
    
    /**
     * 根据用户 ID 查询客户信息
     */
    private CustomerInfo getCustomerInfoByUserId(Long userId) {
        LambdaQueryWrapper<CustomerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerInfo::getUserId, userId);
        return customerInfoService.getOne(wrapper);
    }
    
    /**
     * 转换为 CustomerInfoVO
     */
    private AuthResponse.CustomerInfoVO toCustomerInfoVO(CustomerInfo customerInfo) {
        // 将相对路径转换为完整 URL
        String avatarUrl = null;
        if (customerInfo.getAvatar() != null && !customerInfo.getAvatar().isEmpty()) {
            avatarUrl = ossConfig.getFullUrl(customerInfo.getAvatar());
        }
        
        return AuthResponse.CustomerInfoVO.builder()
                .id(customerInfo.getId())
                .userId(customerInfo.getUserId())
                .username(customerInfo.getUsername())
                .avatar(avatarUrl)  // 返回完整 URL
                .realName(customerInfo.getRealName())
                .gender(customerInfo.getGender())
                .email(customerInfo.getEmail())
                .birthday(customerInfo.getBirthday())
                .build();
    }

    @Override
    public User getByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return getOne(wrapper);
    }

    @Override
    public void updateUserInfo(User user) {
        updateById(user);
    }
    
    /**
     * 刷新 Token
     */
    public AuthResponse refreshToken(String refreshToken) {
        // 验证 Refresh Token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("刷新令牌无效或已过期");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = getById(userId);
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 生成新的双 Token
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .userInfo(AuthResponse.UserInfoVO.builder()
                        .id(user.getId())
                        .phone(user.getPhone())
                        .role(user.getRole())
                        .build())
                .build();
    }

    //待修改
    @Override
    public User getByUsername(String username) {
        return null;
    }
}
