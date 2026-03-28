package com.hs.backend.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 订单操作请求
 */
@Data
public class  OrderActionRequest {

    /**
     * 订单 ID
     */
    @NotNull(message = "订单 ID 不能为空")
    private Long orderId;

    /**
     * 操作类型：cancel-取消订单，pay-支付订单，refund-申请退款
     */
    @NotNull(message = "操作类型不能为空")
    private String actionType;

    /**
     * 取消/退款原因
     */
    private String reason;
}
