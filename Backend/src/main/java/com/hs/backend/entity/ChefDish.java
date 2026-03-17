package com.hs.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 厨师菜品实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_chef_dish")
public class ChefDish implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜品 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 厨师用户 ID
     */
    private Long userId;

    /**
     * 菜品名称
     */
    private String dishName;

    /**
     * 所属菜系 ID
     */
    private Integer cuisineId;

    /**
     * 菜品类型：热菜/凉菜/汤品/主食
     */
    private String dishType;

    /**
     * 菜品单价
     */
    private BigDecimal price;

    /**
     * 菜品描述
     */
    private String description;

    /**
     * 菜品图片 URL
     */
    private String picture;

    /**
     * 是否特色菜：1-是，0-否
     */
    private Integer isFeatured;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
