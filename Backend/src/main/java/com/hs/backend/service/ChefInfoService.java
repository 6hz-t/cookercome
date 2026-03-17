package com.hs.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.vo.ChefAuditVO;

import java.util.List;

/**
 * 厨师服务接口
 */
public interface ChefInfoService extends IService<ChefInfo> {

    /**
     * 分页查询厨师列表
     */
    Page<ChefInfo> getChefPage(Integer page, Integer size, String specialty, Integer level);

    /**
     * 根据 ID 获取厨师详情
     */
    ChefInfo getChefDetail(Long id);

    /**
     * 根据位置搜索附近厨师
     */
    List<ChefInfo> getNearbyChefs(Double longitude, Double latitude, Integer radius);

    /**
     * 创建厨师信息
     */
    ChefInfo createChef(ChefInfo chefInfo);

    /**
     * 更新厨师信息
     */
    void updateChef(ChefInfo chefInfo);

    /**
     * 审核厨师
     */
    void auditChef(Long chefId, Integer status, String reason);

    /**
     * 根据用户 ID 获取厨师信息
     */
    ChefInfo getByUserId(Long userId);

    /**
     * 分页查询厨师审核列表（支持关键词搜索）
     * @param page 页码
     * @param size 每页大小
     * @param keyword 关键词（姓名/手机号）
     * @param auditStatus 审核状态（null 表示全部）
     * @return 分页厨师审核列表
     */
    Page<ChefAuditVO> getChefAuditPage(Integer page, Integer size, String keyword, Integer auditStatus);
}
