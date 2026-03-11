package com.hs.backend.controller;

import com.hs.backend.common.Result;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.UserAddress;
import com.hs.backend.service.PersonalCenterService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
     * 客户控制器
     * 负责用户/客户相关的业务接口
 */
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final PersonalCenterService personalCenterService;

   public CustomerController(PersonalCenterService personalCenterService) {
        this.personalCenterService = personalCenterService;
    }

    /**
     * 获取当前客户个人信息
     */
    @GetMapping("/profile")
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
   public Result<String> deleteAddress(@PathVariable Long id, Principal principal) {
        // TODO: 从 Principal 中获取当前登录用户 ID
        Long userId = 7L;
        personalCenterService.deleteUserAddress(userId, id);
        return Result.success("删除成功");
    }
}
