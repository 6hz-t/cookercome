package com.hs.backend.service;

import com.hs.backend.dto.request.OrderCreateRequest;

/**
 * 客户订单服务接口
 */
public interface CustomerOrderService {
    
    /**
     * 创建订单
     * @param userId 当前登录用户 ID
     * @param request 订单创建请求
     * @return 订单号
     */
    String createOrder(Long userId, OrderCreateRequest request);
}
