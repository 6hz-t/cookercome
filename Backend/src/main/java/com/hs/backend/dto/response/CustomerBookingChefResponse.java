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
     * 厨师等级：1-初级，2-中级，3-高级，4-名厨，5-特级
     */
    private Integer level;
    
    /**
     * 服务次数/完成订单数
     */
    private Integer serviceCount;
    
    /**
     * 起步价
     */
    private BigDecimal basePrice;
    
    /**
     * 简介
     */
    private String introduction;
    
    /**
     * 性别：0-女，1-男
     */
    private Integer gender;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 头像预签名 URL
     */
    private String avatarUrl;
    
    /**
     * 工作年限
     */
    private Integer workYears;
}
