package com.hs.backend.dto.response;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 客户预约厨师响应 DTO
 */
@Data
public class CustomerBookingChefResponse {

    /**
     * 厨师 ID
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 厨师真实姓名
     */
    private String realName;

    /**
     * 审核状态：0-待审核，1-审核通过，2-审核拒绝
     */
    private Integer auditStatus;

    /**
     * 服务次数/完成订单数（从 User 表统计，非数据库字段）
     */
    private Integer serviceCount;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像 URL
     */
    private String avatarUrl;

    /**
     * 烹饪年限
     */
    private Integer experienceYears;

    /**
     * 评分（从 User 表统计，非数据库字段）
     */
    private BigDecimal rating;
}
