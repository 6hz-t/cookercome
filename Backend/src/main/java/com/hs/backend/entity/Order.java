package com.hs.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 客户用户 ID
     */
    private Long customerId;

    /**
     * 厨师用户 ID（chefinfo 表的 user_id）
     */
    private String chefId;

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
     * 菜品定制要求（如：少辣、不放香菜）
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
     * 用户备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
