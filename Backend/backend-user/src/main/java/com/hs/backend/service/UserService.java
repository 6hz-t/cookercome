package com.hs.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hs.backend.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    User register(String username, String password, String phone, Integer userType);

    /**
     * 用户登录
     */
    String login(String username, String password);

    /**
     * 根据用户名查找用户
     */
    User getByUsername(String username);

    /**
     * 根据手机号查找用户
     */
    User getByPhone(String phone);

    /**
     * 更新用户信息
     */
    void updateUserInfo(User user);
}
