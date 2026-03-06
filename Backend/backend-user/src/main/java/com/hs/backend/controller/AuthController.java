package com.hs.backend.controller;

import com.hs.backend.common.Result;
import com.hs.backend.dto.request.LoginRequest;
import com.hs.backend.dto.request.RegisterRequest;
import com.hs.backend.entity.User;
import com.hs.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(
                request.getUsername(),
                request.getPassword(),
                request.getPhone(),
                request.getUserType()
        );
        return Result.success("注册成功", user);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        return Result.success("登录成功", token);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result<User> getCurrentUser() {
        // TODO: 从 SecurityContext 中获取当前用户
        return Result.success(null);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("退出成功");
    }
}
