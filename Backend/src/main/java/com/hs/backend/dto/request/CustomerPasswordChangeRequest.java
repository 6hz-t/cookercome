package com.hs.backend.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 修改密码请求 DTO
 */
@Data
public class CustomerPasswordChangeRequest {
    
    /**
     * 原密码
     */
    @NotBlank(message = "请输入原密码")
    private String oldPassword;
    
    /**
     * 新密码
     */
    @NotBlank(message = "请输入新密码")
    private String newPassword;
}
