package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.backend.entity.ChefDish;
import com.hs.backend.mapper.ChefDishMapper;
import com.hs.backend.service.ChefDishService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 厨师菜品服务实现类
 */
@Tag(name = "菜品查询")
@Service
public class ChefDishServiceImpl extends ServiceImpl<ChefDishMapper, ChefDish> implements ChefDishService {

    @Override
    public Page<ChefDish> getDishPage(Integer page, Integer size, String keyword, Integer cuisineId) {
        Page<ChefDish> dishPage = new Page<>(page, size);

        LambdaQueryWrapper<ChefDish> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（菜品名称）
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ChefDish::getDishName, keyword);
        }

        // 按菜系筛选
        if (cuisineId != null) {
            wrapper.eq(ChefDish::getCuisineId, cuisineId);
        }

        // 按更新时间倒序
        wrapper.orderByDesc(ChefDish::getUpdateTime);

        return page(dishPage, wrapper);
    }
}
