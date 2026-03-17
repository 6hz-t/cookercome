package com.hs.backend.service.impl;

import com.hs.backend.entity.User;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.entity.Order;
import com.hs.backend.mapper.UserMapper;
import com.hs.backend.mapper.ChefInfoMapper;
import com.hs.backend.mapper.OrderMapper;
import com.hs.backend.service.StatisticsService;
import com.hs.backend.vo.StatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ChefInfoMapper chefInfoMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public StatisticsVO getStatistics(Integer days) {
        StatisticsVO vo = new StatisticsVO();

        // 获取用户统计数据
        Long totalUsers = userMapper.selectCount(null);
        vo.setUserCount(totalUsers);

        // 获取厨师统计数据
        Long totalChefs = chefInfoMapper.selectCount(null);
        vo.setChefCount(totalChefs);

        // 获取订单统计数据
        Long totalOrders = orderMapper.selectCount(null);
        vo.setOrderCount(totalOrders);

        // 计算增长率（与前一天相比）
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        Long todayUsers = userMapper.countByDate(today);
        Long yesterdayUsers = userMapper.countByDate(yesterday);
        vo.setUserGrowthRate(calculateGrowthRate(todayUsers, yesterdayUsers));

        Long todayChefs = chefInfoMapper.countByDate(today);
        Long yesterdayChefs = chefInfoMapper.countByDate(yesterday);
        vo.setChefGrowthRate(calculateGrowthRate(todayChefs, yesterdayChefs));

        Long todayOrders = orderMapper.countByDate(today);
        Long yesterdayOrders = orderMapper.countByDate(yesterday);
        vo.setOrderGrowthRate(calculateGrowthRate(todayOrders, yesterdayOrders));

        // 获取趋势数据（使用批量查询优化）
        LocalDate startDate = today.minusDays(days - 1);
        
        List<String> dates = new ArrayList<>();
        List<Long> newUserCounts = new ArrayList<>();
        List<Long> newChefCounts = new ArrayList<>();
        List<Long> newOrderCounts = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        // 批量查询用户、厨师、订单的日期范围数据
        List<Map<String, Object>> userStats = userMapper.countByDateRange(startDate, today);
        List<Map<String, Object>> chefStats = chefInfoMapper.countByDateRange(startDate, today);
        List<Map<String, Object>> orderStats = orderMapper.countByDateRange(startDate, today);

        // 将查询结果转换为 Map 便于查找
        Map<String, Long> userCountMap = convertToMap(userStats);
        Map<String, Long> chefCountMap = convertToMap(chefStats);
        Map<String, Long> orderCountMap = convertToMap(orderStats);

        // 生成日期列表并填充数据
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);
            String dateKey = date.toString(); // YYYY-MM-DD 格式用于查找

            dates.add(dateStr);
            newUserCounts.add(userCountMap.getOrDefault(dateKey, 0L));
            newChefCounts.add(chefCountMap.getOrDefault(dateKey, 0L));
            newOrderCounts.add(orderCountMap.getOrDefault(dateKey, 0L));
        }

        vo.setDates(dates);
        vo.setNewUserCounts(newUserCounts);
        vo.setNewChefCounts(newChefCounts);
        vo.setNewOrderCounts(newOrderCounts);

        return vo;
    }

    /**
     * 将查询结果转换为 Map<日期，数量>
     */
    private Map<String, Long> convertToMap(List<Map<String, Object>> stats) {
        Map<String, Long> result = new HashMap<>();
        for (Map<String, Object> stat : stats) {
            Object dateObj = stat.get("date");
            Object countObj = stat.get("count");
            if (dateObj != null && countObj != null) {
                // date 可能是 LocalDate 或 String 类型
                String dateStr = dateObj.toString();
                Long count = ((Number) countObj).longValue();
                result.put(dateStr, count);
            }
        }
        return result;
    }

    /**
     * 计算增长率
     */
    private Double calculateGrowthRate(Long today, Long yesterday) {
        if (today == null) {
            today = 0L;
        }
        if (yesterday == null || yesterday == 0) {
            return today > 0 ? 100.0 : 0.0;
        }
        return Math.round((double) (today - yesterday) / yesterday * 100 * 10) / 10.0;
    }
}
