package com.hs.backend.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单详情 VO
 */
@Data
public class OrderDetailVO {

    /**
     * 订单 ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 客户 ID
     */
    private Long customerId;

    /**
     * 客户姓名
     */
    private String customerName;

    /**
     * 客户手机号
     */
    private String customerPhone;

    /**
     * 厨师 ID
     */
    private Long chefId;

    /**
     * 厨师姓名
     */
    private String chefName;

    /**
     * 预约时间（格式：yyyy-MM-dd HH:mm）
     */
    private String appointmentTime;

    /**
     * 订单状态：0-待支付，1-待接单，2-已接单，3-服务中，4-已完成，5-已取消，6-已退款
     */
    private Integer status;

    /**
     * 总费用
     */
    private BigDecimal totalFee;

    /**
     * 创建时间（下单时间）
     */
    private LocalDateTime createTime;
}
