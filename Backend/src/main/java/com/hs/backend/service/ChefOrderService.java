package com.hs.backend.service;

import com.hs.backend.dto.OrderDTO;

import java.util.List;
import java.util.Map;

/**
 * Chef-side order service.
 */
public interface ChefOrderService {

    /**
     * History orders for chef.
     */
    List<OrderDTO> getHistoryOrders(Long chefId);

    /**
     * Pending orders that can be accepted.
     */
    List<OrderDTO> getPendingOrders(Long chefId);

    /**
     * Orders that are accepted or in service.
     */
    List<OrderDTO> getServingOrders(Long chefId);

    /**
     * Dashboard summary for today.
     */
    Map<String, Object> getTodayOrderSummary(Long chefId);

    /**
     * Chef accepts one order.
     */
    void acceptOrder(Long orderId, Long chefId);

    /**
     * Update order status by chef workflow.
     */
    void updateOrderStatus(Long orderId, Integer status);

    /**
     * Update chef online/offline status.
     */
    void updateChefStatus(Long chefId, Integer status);
}
