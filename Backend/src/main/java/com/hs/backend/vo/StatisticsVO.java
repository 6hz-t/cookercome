package com.hs.backend.vo;

import lombok.Data;
import java.util.List;

/**
 * 统计数据 VO
 */
@Data
public class StatisticsVO {

    /**
     * 用户总数
     */
    private Long userCount;

    /**
     * 用户增长率
     */
    private Double userGrowthRate;

    /**
     * 厨师总数
     */
    private Long chefCount;

    /**
     * 厨师增长率
     */
    private Double chefGrowthRate;

    /**
     * 订单总数
     */
    private Long orderCount;

    /**
     * 订单增长率
     */
    private Double orderGrowthRate;

    /**
     * 日期列表
     */
    private List<String> dates;

    /**
     * 新增用户数列表
     */
    private List<Long> newUserCounts;

    /**
     * 新增厨师数列表
     */
    private List<Long> newChefCounts;

    /**
     * 新增订单数列表
     */
    private List<Long> newOrderCounts;
}
