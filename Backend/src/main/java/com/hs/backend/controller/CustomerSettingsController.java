package com.hs.backend.controller;

import com.hs.backend.common.Result;
import com.hs.backend.common.service.OssService;
import com.hs.backend.dto.request.CustomerAddressRequest;
import com.hs.backend.dto.request.CustomerPasswordChangeRequest;
import com.hs.backend.dto.request.CustomerPhoneBindRequest;
import com.hs.backend.dto.request.SettingsProfileUpdateRequest;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.UserAddress;
import com.hs.backend.service.CustomerSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
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
     * 上传头像（前端传后端，后端再传 OSS）
     * @param file 头像文件
     * @param principal 当前用户
     * @return 头像完整 URL
     */
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        
        Long userId = getCurrentUserId();
        
        try {
            Map<String, String> uploadResult = ossService.uploadAvatar(file, userId);
            return Result.success(uploadResult);
        } catch (IOException e) {
            log.error("头像上传失败", e);
            return Result.error(500, "上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 绑定新手机号
     */
    @PostMapping("/phone/bind")
    public Result<String> bindPhone(
            @RequestBody @Validated CustomerPhoneBindRequest request,
            Principal principal) {
        
        Long userId = getCurrentUserId();
        customerSettingsService.bindPhone(userId, request.getNewPhone(), request.getCurrentPassword());
        return Result.success("手机号绑定成功");
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/password/change")
    public Result<String> changePassword(
            @RequestBody @Validated CustomerPasswordChangeRequest request,
            Principal principal) {
        
        Long userId = getCurrentUserId();
        customerSettingsService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return Result.success("密码修改成功");
    }
    
    /**
     * 获取用户地址列表
     */
    @GetMapping("/address/list")
    public Result<List<UserAddress>> getAddressList(Principal principal) {
        Long userId = getCurrentUserId();
        List<UserAddress> addressList = customerSettingsService.getAddressList(userId);
        return Result.success(addressList);
    }
    
    /**
     * 添加用户地址
     */
    @PostMapping("/address/add")
    public Result<String> addAddress(
            @RequestBody @Validated CustomerAddressRequest request,
            Principal principal) {
        
        Long userId = getCurrentUserId();
        customerSettingsService.addAddress(userId, request);
        return Result.success("地址添加成功");
    }
    
    /**
     * 更新用户地址
     */
    @PutMapping("/address/update")
    public Result<String> updateAddress(
            @RequestBody @Validated CustomerAddressRequest request,
            Principal principal) {
        
        Long userId = getCurrentUserId();
        customerSettingsService.updateAddress(userId, request);
        return Result.success("地址更新成功");
    }
    
    /**
     * 删除用户地址
     */
    @DeleteMapping("/address/delete/{addressId}")
    public Result<String> deleteAddress(
            @PathVariable Long addressId,
            Principal principal) {
        
        Long userId = getCurrentUserId();
        customerSettingsService.deleteAddress(userId, addressId);
        return Result.success("地址删除成功");
    }
    
    /**
     * 设置默认地址
     */
    @PutMapping("/address/set-default/{addressId}")
    public Result<String> setDefaultAddress(
            @PathVariable Long addressId,
            Principal principal) {
        
        Long userId = getCurrentUserId();
        customerSettingsService.setDefaultAddress(userId, addressId);
        return Result.success("默认地址设置成功");
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
