package com.hs.backend.vo;

import lombok.Data;

/**
 * 仪表盘统计数据 VO
 */
@Data
public class DashboardStatsVO {

    /**
     * 总用户数
     */
    private Long totalUsers;

    /**
     * 用户增长率（百分比，如 12.5 表示 12.5%）
     */
    private Double userGrowthRate;

    /**
     * 待审核厨师数
     */
    private Long pendingChefs;

    /**
     * 今日订单数
     */
    private Long todayOrders;

    /**
     * 订单增长率（百分比，如 5.4 表示 5.4%）
     */
    private Double orderGrowthRate;

    /**
     * 异常订单数
     */
    private Long abnormalOrders;
}
