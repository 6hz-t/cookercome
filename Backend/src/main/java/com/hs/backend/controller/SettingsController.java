package com.hs.backend.controller;

import com.hs.backend.common.Result;
import com.hs.backend.common.service.OssService;
import com.hs.backend.dto.request.SettingsProfileUpdateRequest;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * 设置控制器（客户端）
 */
@RestController
@RequestMapping("/api/customer/settings")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private OssService ossService;

    /**
     * 获取当前用户个人信息
     */
    @GetMapping("/profile")
    public Result<CustomerInfo> getProfile(Principal principal) {
        // TODO: 从 Principal 中获取当前登录用户 ID，临时使用固定 ID 测试
        Long userId = 7L;
        CustomerInfo customerInfo = settingsService.getProfile(userId);
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
        settingsService.updateProfile(userId, request);
        return Result.success("更新成功");
    }

    /**
     * 获取 OSS 上传签名（用于头像直传）
     */
    @GetMapping("/oss/signature")
    public Result<Map<String, String>> getOssSignature(
            @RequestParam String fileName,
            Principal principal) {
        // TODO: 从 Principal 中获取当前登录用户 ID，临时使用固定 ID 测试
        Long userId = 7L;
        
        // 调用 OSS 服务生成签名（5 分钟有效期）
        Map<String, String> signatureInfo = ossService.getUploadSignatureInfo(fileName, userId, 5);
        return Result.success(signatureInfo);
    }
}
