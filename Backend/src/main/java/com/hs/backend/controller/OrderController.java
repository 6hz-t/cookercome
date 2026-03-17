package com.hs.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hs.backend.common.Result;
import com.hs.backend.service.OrderService;
import com.hs.backend.vo.OrderDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 订单控制器
 */
@Tag(name = "订单管理", description = "订单列表查询、订单状态修改等订单相关接口")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 分页查询订单列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取订单列表", description = "分页查询订单列表，支持关键词搜索、状态筛选、时间范围筛选")
    public Result<Page<OrderDetailVO>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        Page<OrderDetailVO> orderPage = orderService.getOrderPage(
            page, size, keyword, status,
            startDate != null ? startDate.toString() : null,
            endDate != null ? endDate.toString() : null
        );
        return Result.success(orderPage);
    }

    /**
     * 强制取消订单
     */
    @PostMapping("/force-cancel/{id}")
    @Operation(summary = "强制取消订单", description = "管理员强制取消异常订单，处理退款")
    public Result<String> forceCancelOrder(
            @PathVariable Long id,
            @RequestParam String reason
    ) {
        boolean success = orderService.forceCancelOrder(id, reason);
        if (success) {
            return Result.success("订单已取消");
        } else {
            return Result.error("订单取消失败");
        }
    }
}
