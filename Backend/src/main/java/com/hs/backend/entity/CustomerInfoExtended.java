package com.hs.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 客户信息扩展实体类（用于个人中心展示）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_customer_info_extended")
public class CustomerInfoExtended implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户信息 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户 ID
     */
    private Long userId;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 会员等级：0-普通，1-白银，2-黄金，3-铂金，4-钻石
     */
    private Integer memberLevel;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 总订单数
     */
    private Integer totalOrders;

    /**
     * 已完成订单数
     */
    private Integer completedOrders;

    /**
     * 累计消费金额
     */
    private java.math.BigDecimal totalSpent;

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
