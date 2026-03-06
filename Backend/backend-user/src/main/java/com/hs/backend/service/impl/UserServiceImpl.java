package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.common.enums.UserTypeEnum;
import com.hs.backend.entity.User;
import com.hs.backend.mapper.UserMapper;
import com.hs.backend.security.JwtTokenProvider;
import com.hs.backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public User register(String username, String password, String phone, Integer userType) {
        // 检查用户名是否存在
        User existingUser = getByUsername(username);
        if (existingUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否存在
        if (StringUtils.hasText(phone)) {
            User existingPhone = getByPhone(phone);
            if (existingPhone != null) {
                throw new BusinessException("手机号已被注册");
            }
        }

        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setUserType(userType != null ? userType : UserTypeEnum.CUSTOMER.getCode());
        user.setStatus(1);

        save(user);
        return user;
    }

    @Override
    public String login(String username, String password) {
        User user = getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 生成 JWT token
        return jwtTokenProvider.generateToken(user.getId());
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
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
}
