package com.hs.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_admin_info") // 对应管理员详情表
public class AdminInfo {
    private Long id;             // 详情表主键
    private Long userId;         // 关联user表的user_id（逻辑外键）
    private String username;     // 管理员用户名
    private String avatar;       // 头像
    private String realName;     // 真实姓名
    private Integer gender;      // 性别：0-未知，1-男，2-女
    private String email;        // 邮箱
    private LocalDate birthday;  // 生日
    private Integer status;      // 状态：0-禁用，1-启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}