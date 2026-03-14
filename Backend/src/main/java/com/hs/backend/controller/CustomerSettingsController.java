package com.hs.backend.controller;

import com.hs.backend.common.Result;
import com.hs.backend.common.service.OssService;
import com.hs.backend.dto.request.AvatarUploadRequest;
import com.hs.backend.dto.request.SettingsProfileUpdateRequest;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.service.CustomerSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

/**
 * 设置控制器（客户端）
 */
@RestController
@RequestMapping("/api/customer/settings")
public class CustomerSettingsController {

    @Autowired
    private CustomerSettingsService customerSettingsService;

    @Autowired
    private OssService ossService;

    /**
     * 获取当前用户个人信息
     */
    @GetMapping("/profile")
    public Result<CustomerInfo> getProfile(Principal principal) {
        // TODO: 从 Principal 中获取当前登录用户 ID，临时使用固定 ID 测试
        Long userId = 7L;
        CustomerInfo customerInfo = customerSettingsService.getProfile(userId);
        return Result.success(customerInfo);
    }

    /**
     * 更新当前用户个人信息
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(
            @RequestBody @Validated SettingsProfileUpdateRequest request,
            Principal principal) {
        // TODO: 从 Principal 中获取当前登录用户 ID
        Long userId = 7L;
        customerSettingsService.updateProfile(userId, request);
        return Result.success("更新成功");
    }

    /**
     * 代理上传头像到 OSS（避免前端 CORS 问题）
     */
    @PostMapping("/avatar/upload")
    public Result<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        // 从 SecurityContext 中获取当前登录用户 ID
        Long userId = getCurrentUserId();
        
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }
        
        try {
            // 调用 OSS 服务代理上传，返回相对路径和完整 URL
            Map<String, String> uploadResult = ossService.uploadFile(file, userId, "avatar");
            // 只返回相对路径给前端存入数据库
            return Result.success(uploadResult);
        } catch (Exception e) {
            return Result.error(500, "上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取 OSS 上传签名（用于头像直传）
     */
    @GetMapping("/oss/signature")
    public Result<Map<String, String>> getOssSignature(
            @RequestParam String fileName,
            Principal principal) {
        // 从 SecurityContext 中获取当前登录用户 ID
        Long userId = getCurrentUserId();
        
        // 调用 OSS 服务生成签名（5 分钟有效期）
        Map<String, String> signatureInfo = ossService.getUploadSignatureInfo(fileName, userId, 5);
        return Result.success(signatureInfo);
    }
    
    /**
     * 从 SecurityContext 中获取当前登录用户 ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            org.springframework.security.core.userdetails.UserDetails userDetails = 
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
            return Long.parseLong(userDetails.getUsername());
        }
        throw new RuntimeException("未找到当前登录用户");
    }
}
