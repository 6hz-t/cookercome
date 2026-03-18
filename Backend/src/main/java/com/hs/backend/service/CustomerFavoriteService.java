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
     * @param chefId 厨师 ID
     */
    void addFavorite(Long customerId, Long chefId);
    
    /**
     * 检查是否已收藏
     * @param customerId 客户 ID
     * @param chefId 厨师 ID
     * @return 是否已收藏
     */
    boolean isFavorited(Long customerId, Long chefId);
    
    /**
     * 获取用户的收藏列表
     * @param customerId 客户 ID
     * @return 收藏的厨师列表
     */
    List<FavoriteChefResponse> getFavoriteList(Long customerId);
    
    /**
     * 取消收藏
     * @param customerId 客户 ID
     * @param chefId 厨师 ID
     */
    void removeFavorite(Long customerId, Long chefId);
}
