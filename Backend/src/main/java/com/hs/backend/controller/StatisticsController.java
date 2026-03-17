package com.hs.backend.controller;

import com.hs.backend.common.Result;
import com.hs.backend.service.StatisticsService;
import com.hs.backend.vo.StatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 统计控制器
 */
@Tag(name = "数据统计", description = "数据统计相关接口")
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取统计数据
     */
    @GetMapping("/overview")
    @Operation(summary = "获取统计数据", description = "获取用户、厨师、订单的总数和增长率，以及趋势数据")
    public Result<StatisticsVO> getStatistics(
            @RequestParam(defaultValue = "30") Integer days
    ) {
        StatisticsVO vo = statisticsService.getStatistics(days);
        return Result.success(vo);
    }
}
