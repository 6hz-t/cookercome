package com.hs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hs.backend.entity.Cuisine;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜系 Mapper 接口
 */
@Mapper
public interface CuisineMapper extends BaseMapper<Cuisine> {

}
