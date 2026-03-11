package com.admin.backendadmin.common.constant;

/**
 * 订单状态常量（与 t_order.status 字段对应）
 */
public class OrderStatusConstant {

    // 待支付
    public static final int ORDER_STATUS_PENDING_PAY = 0;

    // 已支付
    public static final int ORDER_STATUS_PAID = 1;

    // 厨师已接单
    public static final int ORDER_STATUS_CHEF_RECEIVED = 2;

    // 服务中
    public static final int ORDER_STATUS_SERVICEING = 3;

    // 服务完成
    public static final int ORDER_STATUS_COMPLETED = 4;

    // 订单取消
    public static final int ORDER_STATUS_CANCELED = 5;

    // 退款中
    public static final int ORDER_STATUS_REFUNDING = 6;

    // 已退款
    public static final int ORDER_STATUS_REFUNDED = 7;

    private OrderStatusConstant() {
        // 禁止实例化
    }
}
