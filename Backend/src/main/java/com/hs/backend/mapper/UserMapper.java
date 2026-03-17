package com.hs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hs.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 用户 Mapper 接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 按日期统计用户数量
     */
    @Select("SELECT COUNT(*) FROM t_user WHERE DATE(create_time) = #{date}")
    Long countByDate(@Param("date") LocalDate date);

    /**
     * 按日期范围统计用户数量（批量查询）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return List<Map> 包含 date 和 count
     */
    @Select("<script>" +
            "SELECT DATE(create_time) as date, COUNT(*) as count " +
            "FROM t_user " +
            "WHERE DATE(create_time) BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(create_time) " +
            "ORDER BY DATE(create_time)" +
            "</script>")
    List<Map<String, Object>> countByDateRange(@Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate);
}
