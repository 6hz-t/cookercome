package com.hs.backend.controller;

import com.hs.backend.common.Result;
import com.hs.backend.common.service.OssService;
import com.hs.backend.dto.request.SettingsProfileUpdateRequest;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.service.CustomerSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * 客户设置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/customer/settings")
@RequiredArgsConstructor
public class CustomerSettingsController {

    private final CustomerSettingsService customerSettingsService;
    private final OssService ossService;

    /**
     * 获取当前用户个人信息
     */
    @GetMapping("/profile")
    public Result<CustomerInfo> getProfile(Principal principal) {
        // 从 Principal 中获取当前登录用户 ID
        Long userId = getCurrentUserId();
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
        // 从 Principal 中获取当前登录用户 ID
        Long userId = getCurrentUserId();
        customerSettingsService.updateProfile(userId, request);
        return Result.success("更新成功");
    }

    /**
     * 获取 OSS 上传签名（用于头像前端直传）
     * @param filename 文件名
     * @param principal 当前用户
     * @return 签名 URL、完整 URL、相对路径
     */
    @GetMapping("/oss/signature")
    public Result<Map<String, String>> getOssSignature(
            @RequestParam("filename") String fileName,
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
