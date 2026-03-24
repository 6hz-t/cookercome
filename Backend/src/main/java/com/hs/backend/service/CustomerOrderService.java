package com.hs.backend.service;

import com.hs.backend.dto.request.OrderCreateRequest;
import com.hs.backend.dto.response.UserScheduleResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * 客户订单服务接口
 */
public interface CustomerOrderService {
    
    /**
     * 创建订单
     * @param userId 当前登录用户 ID
     * @param request 订单创建请求
     * @return 订单号
     */
    String createOrder(Long userId, OrderCreateRequest request);
    
    /**
     * 获取用户的时间段占用情况（今天开始的未来 15 天）
     * @param userId 用户 ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 时间段占用情况列表
     */
    List<UserScheduleResponse> getUserSchedule(Long userId, LocalDate startDate, LocalDate endDate);
}
