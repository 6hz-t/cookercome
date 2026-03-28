package com.hs.backend.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收藏厨师响应 DTO
 */
@Data
public class FavoriteChefResponse {
    
    /**
     * 收藏 ID
     */
    private Long favoriteId;
    
    /**
     * 厨师 ID（chefinfo 表的 user_id）
     */
    private String chefId;
    
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
     * 头像预签名 URL
     */
    private String avatarUrl;
    
    /**
     * 手机号（从 User 表获取）
     */
    private String phone;
    
    /**
     * 收藏时间
     */
    private LocalDateTime favoriteTime;
}
