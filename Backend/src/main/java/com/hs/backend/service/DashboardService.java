package com.hs.backend.service;

import com.hs.backend.vo.DashboardStatsVO;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {

    /**
     * 获取仪表盘统计数据
     * @return 统计数据 VO
     */
    DashboardStatsVO getStats();
}
