package com.hs.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hs.backend.common.Result;
import com.hs.backend.dto.response.CustomerBookingChefResponse;
import com.hs.backend.service.CustomerBookingChefService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 客户预约厨师控制器
 */
@Tag(name = "客户预约厨师", description = "客户预约厨师相关接口（排序、搜索）")
@Slf4j
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
        log.info("===== 获取厨师列表请求 =====");
        log.info("page={}, size={}, sortBy={}, name={}", page, size, sortBy, name);
        Page<CustomerBookingChefResponse> chefPage = customerBookingChefService.getChefPage(page, size, sortBy, name);
        log.info("===== 查询结果：总数={}, 当前页数据量={}", chefPage.getTotal(), chefPage.getRecords().size());
        return Result.success(chefPage);
    }

    /**
     * 清除厨师列表缓存（管理端操作后调用）
     */
    @PostMapping("/cache/clear")
    @Operation(summary = "清除厨师列表缓存", description = "清除厨师列表缓存，重新从数据库加载")
    public Result<String> clearChefListCache() {
        customerBookingChefService.clearChefListCache();
        return Result.success("缓存已清除");
    }
}
