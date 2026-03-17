package com.hs.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hs.backend.entity.Cuisine;

/**
 * 菜系服务接口
 */
public interface CuisineService extends IService<Cuisine> {

    /**
     * 分页查询菜系列表
     */
    Page<Cuisine> getCuisinePage(Integer page, Integer size, String keyword);

    /**
     * 根据名称查询菜系
     */
    Cuisine getByName(String cuisineName);
}
