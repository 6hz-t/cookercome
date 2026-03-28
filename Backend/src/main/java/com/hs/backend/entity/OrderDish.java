package com.hs.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单菜品实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_order_dish")
public class OrderDish implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联订单 ID
     */
    private Long orderId;

    /**
     * 菜品名称
     */
    private String dishName;

    /**
     * 所属菜系 ID
     */
    private Integer cuisineId;

    /**
     * 菜品单价
     */
    private BigDecimal dishPrice;

    /**
     * 菜品数量
     */
    private Integer quantity;

    /**
     * 菜品定制要求
     */
    private String requirements;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
