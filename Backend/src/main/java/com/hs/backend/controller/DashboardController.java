package com.hs.backend.controller;

import com.hs.backend.common.Result;
import com.hs.backend.service.DashboardService;
import com.hs.backend.vo.DashboardStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表盘控制器
 */
@Tag(name = "仪表盘管理", description = "仪表盘统计数据相关接口")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/stats")
    @Operation(summary = "获取仪表盘统计数据", description = "获取总用户数、待审核厨师、今日订单、异常订单等统计数据")
    public Result<DashboardStatsVO> getStats() {
        DashboardStatsVO stats = dashboardService.getStats();
        return Result.success(stats);
    }
}
