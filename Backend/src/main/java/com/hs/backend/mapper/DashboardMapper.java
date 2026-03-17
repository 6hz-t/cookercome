package com.hs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hs.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 仪表盘 Mapper 接口
 * 核心：通过注解编写统计SQL，适配MySQL数据库（其他数据库需微调日期函数）
 */
@Mapper
public interface DashboardMapper extends BaseMapper<User> {

    /**
     * 统计总用户数
     * @return 总用户数
     */
    @Select("SELECT COUNT(*) FROM t_user ")
    Long countTotalUsers();

    /**
     * 统计今日新增用户数（按创建时间）
     * 注：MySQL用DATE()函数，Oracle用TRUNC()，SQL Server用CONVERT()
     */
    @Select("SELECT COUNT(*) FROM t_user WHERE   DATE(create_time) = CURDATE()")
    Long countTodayUsers();

    /**
     * 统计昨日新增用户数
     */
    @Select("SELECT COUNT(*) FROM t_user WHERE  DATE(create_time) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)")
    Long countYesterdayUsers();

    /**
     * 统计待审核厨师数（audit_status = 0）
     */
    @Select("SELECT COUNT(*) FROM t_chef_info WHERE  audit_status = 0")
    Long countPendingChefs();

    /**
     * 统计今日订单数
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE   DATE(create_time) = CURDATE()")
    Long countTodayOrders();

    /**
     * 统计昨日订单数
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE DATE(create_time) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)")
    Long countYesterdayOrders();

    /**
     * 统计异常订单数（状态为5-已取消/6-已退款）
     * 注：若status是字符串类型，需改为 '5'/'6'
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE  status IN (5, 6)")
    Long countAbnormalOrders();
}