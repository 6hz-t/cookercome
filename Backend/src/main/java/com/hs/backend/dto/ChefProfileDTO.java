package com.hs.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChefProfileDTO {

    private Long id;

    private String userId;

    private String realName;

    private Integer gender;

    private String idCardNo;

    private String phone;

    private String detailAddress;

    private Integer experienceYears;

    private Integer chefLevel;

    private BigDecimal minPrice;

    private Integer completedOrders;

    private String introduction;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Integer status;
}
