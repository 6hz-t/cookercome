package com.hs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hs.backend.entity.Review;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评价 Mapper 接口
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

}
