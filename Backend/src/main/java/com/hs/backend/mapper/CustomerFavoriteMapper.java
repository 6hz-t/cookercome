package com.hs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hs.backend.entity.CustomerFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 收藏 Mapper 接口
 */
@Mapper
public interface CustomerFavoriteMapper extends BaseMapper<CustomerFavorite> {
    
    /**
     * 检查是否已收藏
     * @param customerId 客户 ID
     * @param chefId 厨师 ID（chefinfo 表的 user_id）
     * @return 是否存在
     */
    boolean exists(@Param("customerId") Long customerId, @Param("chefId") String chefId);
}
