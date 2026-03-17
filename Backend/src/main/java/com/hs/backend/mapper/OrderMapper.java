package com.hs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hs.backend.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * 订单 Mapper 接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 按日期统计订单数量
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE DATE(create_time) = #{date}")
    Long countByDate(@Param("date") LocalDate date);
}
