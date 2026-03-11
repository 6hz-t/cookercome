package com.hs.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hs.backend.entity.Chef;

import java.util.List;

/**
 * 厨师服务接口
 */
public interface ChefService extends IService<Chef> {

    /**
     * 分页查询厨师列表
     */
    Page<Chef> getChefPage(Integer page, Integer size, String specialty, Integer level);

    /**
     * 根据 ID 获取厨师详情
     */
    Chef getChefDetail(Long id);

    /**
     * 根据位置搜索附近厨师
     */
    List<Chef> getNearbyChefs(Double longitude, Double latitude, Integer radius);

    /**
     * 创建厨师信息
     */
    Chef createChef(Chef chef);

    /**
     * 更新厨师信息
     */
    void updateChef(Chef chef);

    /**
     * 审核厨师
     */
    void auditChef(Long chefId, Integer status, String reason);
}
