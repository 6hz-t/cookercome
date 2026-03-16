package com.hs.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hs.backend.entity.CustomerInfo;

/**
 * 客户信息服务接口
 */
public interface CustomerInfoService extends IService<CustomerInfo> {

    /**
     * 创建默认客户信息
     * @param userId 用户 ID
     * @param username 用户名
     * @return 客户信息
     */
    CustomerInfo createDefaultCustomerInfo(Long userId, String username);

    /**
     * 根据用户 ID 获取客户信息
     * @param userId 用户 ID
     * @return 客户信息
     */
    CustomerInfo getByUserId(Long userId);
}
