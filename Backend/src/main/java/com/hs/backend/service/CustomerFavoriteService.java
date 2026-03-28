package com.hs.backend.service;

import com.hs.backend.dto.response.FavoriteChefResponse;

import java.util.List;

/**
 * 收藏服务接口
 */
public interface CustomerFavoriteService {
    
    /**
     * 添加收藏
     * @param customerId 客户 ID
     * @param chefId 厨师 ID（chefinfo 表的 user_id）
     */
    void addFavorite(Long customerId, String chefId);
    
    /**
     * 检查是否已收藏
     * @param customerId 客户 ID
     * @param chefId 厨师 ID（chefinfo 表的 user_id）
     * @return 是否已收藏
     */
    boolean isFavorited(Long customerId, String chefId);
    
    /**
     * 获取用户的收藏列表
     * @param customerId 客户 ID
     * @return 收藏的厨师列表
     */
    List<FavoriteChefResponse> getFavoriteList(Long customerId);
    
    /**
     * 取消收藏
     * @param customerId 客户 ID
     * @param chefId 厨师 ID（chefinfo 表的 user_id）
     */
    void removeFavorite(Long customerId, String chefId);
}
