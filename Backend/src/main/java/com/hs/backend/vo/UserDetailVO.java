package com.hs.backend.vo;

import com.hs.backend.entity.AdminInfo;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.entity.CustomerInfo;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户详情统一返回VO（包含基础信息+角色专属信息）
 */
@Data
public class UserDetailVO {
    // 基础用户信息（来自user表）
    private Long userId;         // user表主键
    private String phone;        // 手机号
    private Integer role;        // 角色：1-顾客 2-厨师 3-管理员
    private LocalDateTime createTime; // 创建时间


    // 扩展字段（从角色专属表取值，替代User表缺失的status/name）
    private String status;       // 状态：从角色专属表取（如厨师审核状态/顾客账号状态）
    private String name;         // 姓名：从角色专属表取（管理员realName/厨师chefName/顾客customerName）


    // 角色专属信息（动态赋值）
    private AdminInfo adminInfo;     // 管理员详情（role=3时赋值）
    private ChefInfo chefInfo;       // 厨师详情（role=2时赋值）
    private CustomerInfo customerInfo; // 顾客详情（role=1时赋值）
}
