package com.hs.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 认证响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    /**
     * 访问令牌
     */
    private String accessToken;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
    
    /**
     * 过期时间（秒）
     */
    private Long expiresIn;
    
    /**
     * 用户基础信息
     */
    private UserInfoVO userInfo;
    
    /**
     * 客户详细信息（仅当 role=0 时返回）
     */
    private CustomerInfoVO customerInfo;
    
    /**
     * 用户基础信息 VO（对应 t_user 表）
     */
    @Data
    @Builder
    public static class UserInfoVO {
        private Long id;
        private String phone;
        private Integer role;
    }
    
    /**
     * 客户详细信息 VO（对应 t_customer_info 表）
     */
    @Data
    @Builder
    public static class CustomerInfoVO {
        private Long id;
        private Long userId;
        private String username;
        private String avatar;
        private String realName;
        private Integer gender;
        private String email;
        private LocalDate birthday;
    }
}
