package com.hs.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_dish")
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 厨师 ID
     */
    private Long chefId;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 菜品分类：1-川菜，2-粤菜，3-浙菜，4-苏菜，5-湘菜，6-徽菜，7-闽菜，8-鲁菜
     */
    private Integer category;

    /**
     * 菜品图片 URL
     */
    private String image;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 制作时长（分钟）
     */
    private Integer cookTime;

    /**
     * 辣度：0-不辣，1-微辣，2-中辣，3-特辣
     */
    private Integer spicyLevel;

    /**
     * 口味标签（逗号分隔）
     */
    private String tasteTags;

    /**
     * 食材清单
     */
    private String ingredients;

    /**
     * 制作步骤
     */
    private String steps;

    /**
     * 点单次数
     */
    private Integer orderCount;

    /**
     * 好评数
     */
    private Integer goodReviewCount;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

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
