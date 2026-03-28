package com.hs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hs.backend.entity.OrderDish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单菜品 Mapper 接口
 */
@Mapper
public interface OrderDishMapper extends BaseMapper<OrderDish> {

}
