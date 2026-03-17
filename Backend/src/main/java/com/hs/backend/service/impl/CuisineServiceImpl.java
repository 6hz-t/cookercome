package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.backend.entity.Cuisine;
import com.hs.backend.mapper.CuisineMapper;
import com.hs.backend.service.CuisineService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 菜系服务实现类
 */
@Tag(name = "菜系查询")
@Service
public class CuisineServiceImpl extends ServiceImpl<CuisineMapper, Cuisine> implements CuisineService {

    @Override
    public Page<Cuisine> getCuisinePage(Integer page, Integer size, String keyword) {
        Page<Cuisine> cuisinePage = new Page<>(page, size);

        LambdaQueryWrapper<Cuisine> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Cuisine::getCuisineName, keyword);
        }

        // 按排序值升序
        wrapper.orderByAsc(Cuisine::getSort);

        return page(cuisinePage, wrapper);
    }

    @Override
    public Cuisine getByName(String cuisineName) {
        return getOne(new LambdaQueryWrapper<Cuisine>().eq(Cuisine::getCuisineName, cuisineName));
    }
}
