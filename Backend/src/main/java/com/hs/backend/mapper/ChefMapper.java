package com.hs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hs.backend.entity.Chef;
import org.apache.ibatis.annotations.Mapper;

/**
 * 厨师 Mapper 接口
 */
@Mapper
public interface ChefMapper extends BaseMapper<Chef> {

}
