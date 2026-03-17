package com.hs.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hs.backend.entity.Order;
import com.hs.backend.vo.OrderDetailVO;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {

    /**
     * 分页查询订单列表（支持筛选）
     * @param page 页码
     * @param size 每页大小
     * @param keyword 关键词（订单号/用户 ID）
     * @param status 订单状态（null 表示全部）
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 分页订单列表
     */
    Page<OrderDetailVO> getOrderPage(Integer page, Integer size, String keyword, Integer status, String startDate, String endDate);

    /**
     * 强制取消订单
     * @param orderId 订单 ID
     * @param reason 取消原因
     * @return 是否成功
     */
    boolean forceCancelOrder(Long orderId, String reason);
}
