package com.hs.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hs.backend.entity.ChefDish;

/**
 * 厨师菜品服务接口
 */
public interface ChefDishService extends IService<ChefDish> {

    /**
     * 分页查询菜品列表
     */
    Page<ChefDish> getDishPage(Integer page, Integer size, String keyword, Integer cuisineId);
}
