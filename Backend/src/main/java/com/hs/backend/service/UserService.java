package com.hs.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hs.backend.dto.response.AuthResponse;
import com.hs.backend.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    User register(String phone, String password, Integer role);

    /**
     * 用户登录（返回双 Token）
     */
    AuthResponse login(String phone, String password);
    
    /**
     * 刷新 Token
     */
    AuthResponse refreshToken(String refreshToken);


    /**
     * 根据手机号查找用户
     */
    User getByPhone(String phone);

    /**
     * 更新用户信息
     */
    void updateUserInfo(User user);
}
