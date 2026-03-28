package com.hs.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hs.backend.dto.response.CustomerBookingChefResponse;

/**
 * 客户预约厨师服务接口
 */
public interface CustomerBookingChefService {

    /**
     * 分页查询厨师列表（支持排序和搜索）
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段（level_desc-等级降序，price_asc-价格升序，orders_desc-订单数降序）
     * @param name 厨师姓名（模糊搜索）
     * @return 厨师列表分页数据
     */
    Page<CustomerBookingChefResponse> getChefPage(Integer page, Integer size, String sortBy, String name);

    /**
     * 清除厨师列表缓存
     */
    void clearChefListCache();
}
