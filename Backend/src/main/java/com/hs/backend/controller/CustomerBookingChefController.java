package com.hs.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hs.backend.common.Result;
import com.hs.backend.dto.response.CustomerBookingChefResponse;
import com.hs.backend.service.CustomerBookingChefService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 客户预约厨师控制器
 */
@Tag(name = "客户预约厨师", description = "客户预约厨师相关接口（排序、搜索）")
@RestController
@RequestMapping("/api/customer/booking/chef")
public class CustomerBookingChefController {
    
    private final CustomerBookingChefService customerBookingChefService;
    
    public CustomerBookingChefController(CustomerBookingChefService customerBookingChefService) {
        this.customerBookingChefService = customerBookingChefService;
    }
    
    /**
     * 分页查询厨师列表（支持排序和搜索）
     */
    @GetMapping("/list")
    @Operation(summary = "获取厨师列表", description = "获取厨师列表信息，支持按等级、价格、订单数排序和名字搜索")
    public Result<Page<CustomerBookingChefResponse>> getChefList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String name) {
        Page<CustomerBookingChefResponse> chefPage = customerBookingChefService.getChefPage(page, size, sortBy, name);
        return Result.success(chefPage);
    }
}
