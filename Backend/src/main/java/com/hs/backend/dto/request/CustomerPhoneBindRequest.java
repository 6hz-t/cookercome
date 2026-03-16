package com.hs.backend.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 客户手机号绑定请求 DTO
 */
@Data
public class CustomerPhoneBindRequest {
    
    /**
     * 新手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String newPhone;
    
    /**
     * 当前密码（用于验证身份）
     */
    @NotBlank(message = "请输入当前密码")
    private String currentPassword;
}
