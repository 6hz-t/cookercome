package com.hs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hs.backend.entity.ChefInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * 厨师 Mapper 接口
 */
@Mapper
public interface ChefInfoMapper extends BaseMapper<ChefInfo> {

    /**
     * 按日期统计厨师数量
     */
    @Select("SELECT COUNT(*) FROM t_chef_info WHERE DATE(create_time) = #{date}")
    Long countByDate(@Param("date") LocalDate date);
}
