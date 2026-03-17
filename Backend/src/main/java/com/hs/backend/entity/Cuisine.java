package com.hs.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 菜系实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_cuisine")
public class Cuisine implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜系 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 菜系名称
     */
    private String cuisineName;

    /**
     * 菜系描述
     */
    private String description;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
}
