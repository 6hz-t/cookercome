package com.hs.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评价实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("review")
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单 ID
     */
    private Long orderId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 厨师 ID
     */
    private Long chefId;

    /**
     * 评分（1-5 星）
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 图片列表（逗号分隔）
     */
    private String images;

    /**
     * 口味评分
     */
    private Integer tasteScore;

    /**
     * 服务评分
     */
    private Integer serviceScore;

    /**
     * 性价比评分
     */
    private Integer valueScore;

    /**
     * 回复内容
     */
    private String reply;

    /**
     * 回复时间
     */
    private LocalDateTime replyTime;

    /**
     * 状态：0-未回复，1-已回复
     */
    private Integer status;

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
