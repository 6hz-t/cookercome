package com.hs.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 订单创建请求 DTO
 */
@Data
@Schema(description = "订单创建请求")
public class OrderCreateRequest {

    /**
     * 厨师 ID（chefinfo 表的 user_id）
     */
    @NotNull(message = "厨师 ID 不能为空")
    @Schema(description = "厨师 ID", required = true, example = "41")
    private String chefId;

    /**
     * 地址 ID
     */
    @NotNull(message = "地址 ID 不能为空")
    @Schema(description = "地址 ID", required = true, example = "1")
    private Long addressId;

    /**
     * 预约日期
     */
    @NotNull(message = "预约日期不能为空")
    @Schema(description = "预约日期", required = true, example = "2026-03-20")
    private String reserveDate;

    /**
     * 预约时间段
     */
    @NotBlank(message = "预约时间不能为空")
    @Schema(description = "预约时间段", required = true, example = "11:00-13:00")
    private String reserveTime;

    /**
     * 菜品要求
     */
    @Schema(description = "菜品要求", example = "少辣、不放香菜")
    private String dishRequirements;

    /**
     * 订单总金额
     */
    @NotNull(message = "订单金额不能为空")
    @DecimalMin(value = "0.01", message = "订单金额必须大于 0")
    @Schema(description = "订单总金额", required = true, example = "200.00")
    private BigDecimal totalAmount;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "请准时到达")
    private String remark;
}
