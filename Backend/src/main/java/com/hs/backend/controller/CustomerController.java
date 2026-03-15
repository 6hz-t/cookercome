package com.hs.backend.controller;

import com.hs.backend.common.Result;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.UserAddress;
import com.hs.backend.service.CustomerPersonalCenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
     * 客户控制器
     * 负责用户/客户相关的业务接口
 */
@Tag(name = "客户管理", description = "客户个人信息、收货地址等客户相关接口")
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerPersonalCenterService personalCenterService;

   public CustomerController(CustomerPersonalCenterService personalCenterService) {
        this.personalCenterService = personalCenterService;
    }

    /**
     * 获取当前客户个人信息
     */
    @GetMapping("/profile")
    @Operation(summary = "获取客户个人信息", description = "获取当前登录客户的个人信息")
   public Result<CustomerInfo> getProfile(Principal principal) {
        // TODO: 从 Principal 中获取当前登录用户 ID
        // 临时使用固定 ID 测试
        Long userId = 7L;
        CustomerInfo customerInfo = personalCenterService.getCustomerProfile(userId);
        return Result.success(customerInfo);
    }

    /**
     * 更新客户个人信息
     */
    @PutMapping("/profile")
    @Operation(summary = "更新客户个人信息", description = "更新当前登录客户的个人信息")
   public Result<String> updateProfile(@RequestBody CustomerInfo customerInfo, Principal principal) {
        // TODO: 从 Principal 中获取当前登录用户 ID
        Long userId = 7L;
        personalCenterService.updateCustomerProfile(userId, customerInfo);
        return Result.success("更新成功");
    }

    /**
     * 获取客户的收货地址列表
     */
    @GetMapping("/addresses")
    @Operation(summary = "获取收货地址列表", description = "获取当前登录客户的所有收货地址")
   public Result<List<UserAddress>> getAddresses(Principal principal) {
        // TODO: 从 Principal 中获取当前登录用户 ID
        Long userId = 7L;
        List<UserAddress> addresses = personalCenterService.getUserAddresses(userId);
        return Result.success(addresses);
    }

    /**
     * 添加收货地址
     */
    @PostMapping("/addresses")
    @Operation(summary = "添加收货地址", description = "为当前登录客户添加新的收货地址")
   public Result<String> addAddress(@RequestBody UserAddress address, Principal principal) {
        // TODO: 从 Principal 中获取当前登录用户 ID
        Long userId = 7L;
        personalCenterService.addUserAddress(userId, address);
        return Result.success("添加成功");
    }

    /**
     * 更新收货地址
     */
    @PutMapping("/addresses/{id}")
    @Operation(summary = "更新收货地址", description = "更新指定 ID 的收货地址信息")
   public Result<String> updateAddress(@PathVariable Long id, @RequestBody UserAddress address, Principal principal) {
        // TODO: 从 Principal 中获取当前登录用户 ID
        Long userId = 7L;
        personalCenterService.updateUserAddress(userId, address);
        return Result.success("更新成功");
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("/addresses/{id}")
    @Operation(summary = "删除收货地址", description = "删除指定 ID 的收货地址")
   public Result<String> deleteAddress(@PathVariable Long id, Principal principal) {
        // TODO: 从 Principal 中获取当前登录用户 ID
        Long userId = 7L;
        personalCenterService.deleteUserAddress(userId, id);
        return Result.success("删除成功");
    }
}
