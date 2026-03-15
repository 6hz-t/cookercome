package com.hs.backend.service;

import com.hs.backend.dto.request.SettingsProfileUpdateRequest;
import com.hs.backend.entity.CustomerInfo;

/**
 * 设置服务接口
 */
public interface CustomerSettingsService {

    /**
     * 获取用户个人信息
     * @param userId 用户 ID
     * @return 客户信息
     */
    CustomerInfo getProfile(Long userId);

    /**
     * 更新用户个人信息
     * @param userId 用户 ID
     * @param request 更新请求
     */
    void updateProfile(Long userId, SettingsProfileUpdateRequest request);

    /**
     * 绑定新手机号（需要验证当前密码）
     * @param userId 用户 ID
     * @param newPhone 新手机号
     * @param currentPassword 当前密码
     */
    void bindPhone(Long userId, String newPhone, String currentPassword);

    /**
     * 修改密码
     * @param userId 用户 ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 获取用户地址列表
     * @param userId 用户 ID
     * @return 地址列表
     */
    java.util.List<com.hs.backend.entity.UserAddress> getAddressList(Long userId);

    /**
     * 添加用户地址
     * @param userId 用户 ID
     * @param request 地址信息
     */
    void addAddress(Long userId, com.hs.backend.dto.request.CustomerAddressRequest request);

    /**
     * 更新用户地址
     * @param userId 用户 ID
     * @param request 地址信息
     */
    void updateAddress(Long userId, com.hs.backend.dto.request.CustomerAddressRequest request);

    /**
     * 删除用户地址
     * @param userId 用户 ID
     * @param addressId 地址 ID
     */
    void deleteAddress(Long userId, Long addressId);

    /**
     * 设置默认地址
     * @param userId 用户 ID
     * @param addressId 地址 ID
     */
    void setDefaultAddress(Long userId, Long addressId);
}
