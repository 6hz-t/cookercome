package com.hs.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单实体类（与 t_order 表结构完全匹配）
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
    @TableField("order_no")
    private String orderNo;

    /**
     * 用户 ID（数据库字段为 customer_id）
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 厨师 ID
     */
    @TableField("chef_id")
    private Long chefId;

    /**
     * 服务地址 ID
     */
    @TableField("address_id")
    private Long addressId;

    /**
     * 预约日期（数据库字段为 reserve_date）
     */
    @TableField("reserve_date")
    private LocalDate reserveDate;

    /**
     * 预约时间（数据库字段为 reserve_time）
     */
    @TableField("reserve_time")
    private String reserveTime;

    /**
     * 菜品要求（数据库字段为 dish_requirements）
     */
    @TableField("dish_requirements")
    private String dishRequirements;

    /**
     * 总金额（数据库字段为 total_amount）
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 订单状态：0-待支付，1-待接单，2-已接单，3-服务中，4-已完成，5-已取消，6-已退款
     */
    private Integer status;

    /**
     * 支付时间
     */
    @TableField("payment_time")
    private LocalDateTime paymentTime;

    /**
     * 服务开始时间（数据库字段为 service_start_time）
     */
    @TableField("service_start_time")
    private LocalDateTime serviceStartTime;

    /**
     * 服务结束时间（数据库字段为 service_end_time）
     */
    @TableField("service_end_time")
    private LocalDateTime serviceEndTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Integer deleted;
}