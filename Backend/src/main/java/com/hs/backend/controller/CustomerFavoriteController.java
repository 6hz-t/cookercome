package com.hs.backend.controller;

import com.hs.backend.common.Result;
import com.hs.backend.dto.response.FavoriteChefResponse;
import com.hs.backend.service.CustomerFavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收藏控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/customer/favorite")
@RequiredArgsConstructor
public class CustomerFavoriteController {
    
    private final CustomerFavoriteService customerFavoriteService;
    
    /**
     * 添加收藏
     * @param chefId 厨师 ID（chefinfo 表的 user_id）
     * @return 操作结果
     */
    @PostMapping("/add/{chefId}")
    public Result<String> addFavorite(@PathVariable String chefId) {
        Long customerId = getCurrentUserId();
        customerFavoriteService.addFavorite(customerId, chefId);
        return Result.success("收藏成功");
    }
    
    /**
     * 检查是否已收藏
     * @param chefId 厨师 ID（chefinfo 表的 user_id）
     * @return 是否已收藏
     */
    @GetMapping("/check/{chefId}")
    public Result<Map<String, Boolean>> checkFavorite(@PathVariable String chefId) {
        Long customerId = getCurrentUserId();
        boolean isFavorited = customerFavoriteService.isFavorited(customerId, chefId);
        
        Map<String, Boolean> result = new HashMap<>();
        result.put("isFavorited", isFavorited);
        return Result.success(result);
    }
    
    /**
     * 从 SecurityContext 中获取当前登录用户 ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            org.springframework.security.core.userdetails.UserDetails userDetails = 
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
            return Long.parseLong(userDetails.getUsername());
        }
        throw new RuntimeException("未找到当前登录用户");
    }
    
    /**
     * 获取用户的收藏列表
     * @return 收藏的厨师列表
     */
    @GetMapping("/list")
    public Result<List<FavoriteChefResponse>> getFavoriteList() {
        Long customerId = getCurrentUserId();
        List<FavoriteChefResponse> favoriteList = customerFavoriteService.getFavoriteList(customerId);
        return Result.success(favoriteList);
    }
    
    /**
     * 取消收藏
     * @param chefId 厨师 ID（chefinfo 表的 user_id）
     * @return 操作结果
     */
    @PostMapping("/remove/{chefId}")
    public Result<String> removeFavorite(@PathVariable String chefId) {
        Long customerId = getCurrentUserId();
        customerFavoriteService.removeFavorite(customerId, chefId);
        return Result.success("取消收藏成功");
    }
}
