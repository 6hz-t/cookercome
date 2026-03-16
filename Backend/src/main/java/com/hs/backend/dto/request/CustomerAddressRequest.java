package com.hs.backend.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 用户地址添加/更新请求 DTO
 */
@Data
public class CustomerAddressRequest {
    
    /**
     * 地址 ID（更新时必填）
     */
    private Long id;
    
    /**
     * 收件人/联系人
     */
    @NotBlank(message = "请输入收件人姓名")
    private String receiver;
    
    /**
     * 联系电话
     */
    @NotBlank(message = "请输入联系电话")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 省份
     */
    @NotBlank(message = "请选择省份")
    private String province;
    
    /**
     * 城市
     */
    @NotBlank(message = "请选择城市")
    private String city;
    
    /**
     * 区县
     */
    @NotBlank(message = "请选择区县")
    private String district;
    
    /**
     * 详细地址
     */
    @NotBlank(message = "请输入详细地址")
    private String detailAddress;
    
    /**
     * 地址纬度（可选）
     */
    private Double latitude;
    
    /**
     * 地址经度（可选）
     */
    private Double longitude;
    
    /**
     * 是否默认地址：1-是，0-否
     */
    private Integer isDefault = 0;
}
