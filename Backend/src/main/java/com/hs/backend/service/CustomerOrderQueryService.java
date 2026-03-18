package com.hs.backend.service;

import com.hs.backend.dto.OrderDTO;

import java.util.List;

/**
 * 客户订单服务接口
 */
public interface CustomerOrderQueryService {

    /**
     * 获取用户订单列表（支持分类查询）
     * 
     * @param userId 用户 ID
     * @param category 订单分类：all-全部 pending-预约中 payment-待支付 fulfillment-待履约 refunding-退款中 history-历史订单
     * @return 订单列表
     */
    List<OrderDTO> getUserOrders(Long userId, String category);

    /**
     * 取消订单
     * @param userId 用户 ID
     * @param orderId 订单 ID
     * @param reason 取消原因
     */
    void cancelOrder(Long userId, Long orderId, String reason);

    /**
     * 支付订单
     * @param userId 用户 ID
     * @param orderId 订单 ID
     */
    void payOrder(Long userId, Long orderId);

    /**
     * 申请退款
     * @param userId 用户 ID
     * @param orderId 订单 ID
     * @param reason 退款原因
     */
    void refundOrder(Long userId, Long orderId, String reason);
}
