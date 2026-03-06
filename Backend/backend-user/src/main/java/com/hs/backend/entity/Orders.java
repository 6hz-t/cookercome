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
@TableName("orders")
public class Orders implements Serializable {

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
     * 用户 ID
     */
    private Long userId;

    /**
     * 厨师 ID
     */
    private Long chefId;

    /**
     * 服务地址 ID
     */
    private Long addressId;

    /**
     * 预约时间
     */
    private LocalDateTime appointmentTime;

    /**
     * 预计服务时长（小时）
     */
    private Integer duration;

    /**
     * 用餐人数
     */
    private Integer guestCount;

    /**
     * 菜品要求
     */
    private String dishRequirement;

    /**
     * 口味要求
     */
    private String tasteRequirement;

    /**
     * 食材方式：1-厨师代购，2-用户自备
     */
    private Integer ingredientMode;

    /**
     * 食材费用
     */
    private BigDecimal ingredientFee;

    /**
     * 服务费
     */
    private BigDecimal serviceFee;

    /**
     * 总费用
     */
    private BigDecimal totalFee;

    /**
     * 订单状态：0-待支付，1-待接单，2-已接单，3-服务中，4-已完成，5-已取消，6-已退款
     */
    private Integer status;

    /**
     * 支付方式：1-微信，2-支付宝
     */
    private Integer paymentMethod;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 交易流水号
     */
    private String transactionId;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 备注
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

    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Integer deleted;
}
