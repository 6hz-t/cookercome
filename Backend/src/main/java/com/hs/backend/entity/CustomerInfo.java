package com.hs.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 客户专属信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_customer_info")
public class CustomerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户信息 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户 ID（对应 t_user.id）
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;
    
    /**
     * 手机号（从 User 表关联查询，非数据库字段）
     */
    @TableField(exist = false)
    private String phone;

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
