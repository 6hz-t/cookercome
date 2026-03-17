package com.hs.backend.service;

import com.hs.backend.vo.StatisticsVO;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取统计数据
     * @param days 天数（7/30/90）
     * @return 统计数据 VO
     */
    StatisticsVO getStatistics(Integer days);
}
