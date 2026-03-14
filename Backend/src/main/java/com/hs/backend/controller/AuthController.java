package com.hs.backend.controller;

import com.hs.backend.common.Result;
import com.hs.backend.dto.request.LoginRequest;
import com.hs.backend.dto.request.RefreshTokenRequest;
import com.hs.backend.dto.request.RegisterRequest;
import com.hs.backend.dto.response.AuthResponse;
import com.hs.backend.entity.User;
import com.hs.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户注册、登录、Token 刷新等认证相关接口")
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
    @Operation(summary = "用户注册", description = "新用户通过手机号和密码进行注册")
    public Result<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(
                request.getPhone(),  // 使用手机号
                request.getPassword(),
                request.getRole()  // 传递角色参数
        );
        return Result.success("注册成功", user);
    }

    /**
     * 用户登录（双 Token）
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户通过手机号和密码登录，返回 Access Token 和 Refresh Token")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request.getPhone(), request.getPassword());
        return Result.success("登录成功", response);
    }
    
    /**
     * 刷新 Token
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新 Token", description = "使用 Refresh Token 获取新的 Access Token 和 Refresh Token")
    public Result<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = userService.refreshToken(request.getRefreshToken());
        return Result.success("刷新成功", response);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<User> getCurrentUser() {
        // TODO: 从 SecurityContext 中获取当前用户
        return Result.success(null);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "用户退出登录，使 Token 失效")
    public Result<String> logout() {
        return Result.success("退出成功");
    }
}
