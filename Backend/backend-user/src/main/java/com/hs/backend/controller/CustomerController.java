package com.hs.backend.controller;

import com.hs.backend.common.Result;
import com.hs.backend.entity.User;
import com.hs.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * 客户控制器
 * 负责用户/客户相关的业务接口
 */
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final UserService userService;

   public CustomerController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/list")
    public Result<String> getChefList() {
        return Result.success("成功调用");}
    /**
     * 获取当前客户个人信息
     *
     *
     *
     */
    @GetMapping("/profile")
   public Result<User> getProfile() {
        // TODO: 从 SecurityContext 中获取当前登录用户
       return Result.success(null);
    }

    /**
     * 更新客户个人信息
     */
    @PutMapping("/profile")
   public Result<String> updateProfile(@RequestBody User user) {
        // TODO: 实现更新逻辑
       return Result.success("更新成功");
    }

    /**
     * 获取客户的订单列表
     */
    @GetMapping("/orders")
   public Result<String> getOrders() {
        // TODO: 实现获取订单逻辑
       return Result.success("成功调用");
    }

    /**
     * 获取客户的收藏列表
     */
    @GetMapping("/favorites")
   public Result<String> getFavorites() {
        // TODO: 实现获取收藏逻辑
       return Result.success("成功调用");
    }

    /**
     * 获取客户的收货地址列表
     */
    @GetMapping("/addresses")
   public Result<String> getAddresses() {
        // TODO: 实现获取地址逻辑
       return Result.success("成功调用");
    }

    /**
     * 添加收货地址
     */
    @PostMapping("/addresses")
   public Result<String> addAddress(@RequestBody String address) {
        // TODO: 实现添加地址逻辑
       return Result.success("添加成功");
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("/addresses/{id}")
   public Result<String> deleteAddress(@PathVariable Long id) {
        // TODO: 实现删除地址逻辑
       return Result.success("删除成功");
    }

    /**
     * 更新收货地址
     */
    @PutMapping("/addresses/{id}")
   public Result<String> updateAddress(@PathVariable Long id, @RequestBody String address) {
        // TODO: 实现更新地址逻辑
       return Result.success("更新成功");
    }
}
