package com.hs.backend.service;

import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.UserAddress;

import java.util.List;

/**
 * 个人中心服务接口
 */
public interface PersonalCenterService {

    /**
     * 获取当前客户个人信息
     */
    CustomerInfo getCustomerProfile(Long userId);

    /**
     * 更新客户个人信息
     */
    void updateCustomerProfile(Long userId, CustomerInfo customerInfo);

    /**
     * 获取客户的收货地址列表
     */
    List<UserAddress> getUserAddresses(Long userId);

    /**
     * 添加收货地址
     */
    void addUserAddress(Long userId, UserAddress address);

    /**
     * 更新收货地址
     */
    void updateUserAddress(Long userId, UserAddress address);

    /**
     * 删除收货地址
     */
    void deleteUserAddress(Long userId, Long addressId);

    /**
     * 设置默认地址
     */
    void setDefaultAddress(Long userId, Long addressId);
}
