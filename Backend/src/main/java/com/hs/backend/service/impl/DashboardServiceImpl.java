package com.hs.backend.service.impl;

import com.hs.backend.mapper.DashboardMapper;
import com.hs.backend.service.DashboardService;
import com.hs.backend.vo.DashboardStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 仪表盘服务实现类
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Override
    public DashboardStatsVO getStats() {
        DashboardStatsVO vo = new DashboardStatsVO();

        // 1. 总用户数
        Long totalUsers = dashboardMapper.countTotalUsers();
        vo.setTotalUsers(totalUsers != null ? totalUsers : 0L);

        // 2. 用户增长率 = (今日新增 - 昨日新增) / 昨日新增 * 100%
        Long todayUsers = dashboardMapper.countTodayUsers();
        Long yesterdayUsers = dashboardMapper.countYesterdayUsers();
        Double userGrowthRate = calculateGrowthRate(todayUsers, yesterdayUsers);
        vo.setUserGrowthRate(userGrowthRate);

        // 3. 待审核厨师数（audit_status = 0）
        Long pendingChefs = dashboardMapper.countPendingChefs();
        vo.setPendingChefs(pendingChefs != null ? pendingChefs : 0L);

        // 4. 今日订单数
        Long todayOrders = dashboardMapper.countTodayOrders();
        vo.setTodayOrders(todayOrders != null ? todayOrders : 0L);

        // 5. 订单增长率 = (今日订单 - 昨日订单) / 昨日订单 * 100%
        Long yesterdayOrders = dashboardMapper.countYesterdayOrders();
        Double orderGrowthRate = calculateGrowthRate(todayOrders, yesterdayOrders);
        vo.setOrderGrowthRate(orderGrowthRate);

        // 6. 异常订单数（状态为 5-已取消 或 6-已退款）
        Long abnormalOrders = dashboardMapper.countAbnormalOrders();
        vo.setAbnormalOrders(abnormalOrders != null ? abnormalOrders : 0L);

        return vo;
    }

    /**
     * 计算增长率
     * @param today 今日数据
     * @param yesterday 昨日数据
     * @return 增长率（百分比），昨日为 0 时返回 100.0（表示从无到有）
     */
    private Double calculateGrowthRate(Long today, Long yesterday) {
        if (today == null) {
            today = 0L;
        }
        if (yesterday == null || yesterday == 0) {
            // 昨日为 0 时，今日有数据则返回 100%，否则返回 0%
            return today > 0 ? 100.0 : 0.0;
        }
        // 增长率 = (今日 - 昨日) / 昨日 * 100
        return Math.round((double) (today - yesterday) / yesterday * 100 * 10) / 10.0;
    }
}
