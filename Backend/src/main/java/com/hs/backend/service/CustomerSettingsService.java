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
}
