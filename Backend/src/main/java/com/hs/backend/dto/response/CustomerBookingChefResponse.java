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
    private String userId;
    
    /**
     * 厨师真实姓名
     */
    private String realName;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;
    
    /**
     * 厨师等级：1-初级厨师，2-中级厨师，3-高级厨师，4-资深厨师，5-特级厨师
     */
    private Integer chefLevel;
    
    /**
     * 烹饪年限
     */
    private Integer experienceYears;
    
    /**
     * 已完成订单数
     */
    private Integer completedOrders;
    
    /**
     * 最低服务价格（元）
     */
    private BigDecimal minPrice;
    
    /**
     * 厨师简介
     */
    private String introduction;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 头像预签名 URL
     */
    private String avatarUrl;
}
