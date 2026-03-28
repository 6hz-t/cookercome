package com.hs.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单菜品数据传输对象
 */
@Data
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 菜品规格（如：大份/中份/小份）
     */
    private String spec;

    /**
     * 菜品单价
     */
    private BigDecimal price;

    /**
     * 菜品数量
     */
    private Integer quantity;

    /**
     * 定制要求
     */
    private String requirements;
}
