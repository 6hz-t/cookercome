package com.hs.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单数据传输对象
 */
@Data
public class OrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 厨师用户 ID（chefinfo 表的 user_id）
     */
    private String chefId;

    /**
     * 厨师姓名
     */
    private String chefName;

    /**
     * 厨师头像
     */
    private String chefAvatar;

    /**
     * 厨师等级
     */
    private Integer chefLevel;

    /**
     * 服务地址 ID
     */
    private Long addressId;

    /**
     * 预约日期
     */
    private LocalDateTime reserveDate;

    /**
     * 预约时间段（如：11:00-13:00）
     */
    private String reserveTime;

    /**
     * 菜品定制要求
     */
    private String dishRequirements;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态：0-订单提交 1-待支付，2-已支付，3-服务完成，4-订单取消，5-退款中，6-已退款
     */
    private Integer status;

    /**
     * 用户备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 服务开始时间
     */
    private LocalDateTime serviceStartTime;

    /**
     * 服务结束时间
     */
    private LocalDateTime serviceEndTime;

    /**
     * 是否已收藏（true-已收藏，false-未收藏）
     */
    private Boolean isFavorited;

    /**
     * 客户姓名
     */
    private String customerName;

    /**
     * 客户联系电话
     */
    private String customerPhone;

    /**
     * 服务地址全称
     */
    private String address;
}
