package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.dto.OrderDTO;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.entity.CustomerFavorite;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.Order;
import com.hs.backend.mapper.ChefInfoMapper;
import com.hs.backend.mapper.CustomerFavoriteMapper;
import com.hs.backend.mapper.CustomerInfoMapper;
import com.hs.backend.mapper.OrderMapper;
import com.hs.backend.service.CustomerOrderQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 客户订单查询服务实现
 */
@Slf4j
public class CustomerOrderQueryServiceImpl implements CustomerOrderQueryService {

    private final OrderMapper orderMapper;

    private final ChefInfoMapper chefInfoMapper;

    private final CustomerInfoMapper customerInfoMapper;

    private final CustomerFavoriteMapper customerFavoriteMapper;

    public CustomerOrderQueryServiceImpl(OrderMapper orderMapper,
                                         ChefInfoMapper chefInfoMapper,
                                         CustomerInfoMapper customerInfoMapper,
                                         CustomerFavoriteMapper customerFavoriteMapper) {
        this.orderMapper = orderMapper;
        this.chefInfoMapper = chefInfoMapper;
        this.customerInfoMapper = customerInfoMapper;
        this.customerFavoriteMapper = customerFavoriteMapper;
    }

    @Override
    public List<OrderDTO> getUserOrders(Long userId, String category) {
        List<Order> orders = queryOrdersFromDB(userId, category);
        return convertToDTOs(orders, userId);
    }

    /**
     * 从数据库查询订单。
     */
    private List<Order> queryOrdersFromDB(Long userId, String category) {
        List<Integer> statusList = getStatusListByCategory(category);

        if (statusList == null || statusList.isEmpty()) {
            return new ArrayList<>();
        }

        return orderMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                .eq(Order::getCustomerId, userId)
                .in(Order::getStatus, statusList)
                .orderByDesc(Order::getCreateTime));
    }

    /**
     * 根据分类获取状态列表。
     */
    private List<Integer> getStatusListByCategory(String category) {
        switch (category) {
            case "all":
                return List.of(0, 1, 2, 3, 4, 5, 6);
            case "pending":
                return List.of(0);
            case "payment":
                return List.of(1);
            case "fulfillment":
                return List.of(2);
            case "refunding":
                return List.of(5);
            case "history":
                return List.of(3, 4, 6);
            default:
                return List.of(0, 1, 2);
        }
    }

    /**
     * 转换为 DTO 并带收藏标记。
     */
    private List<OrderDTO> convertToDTOs(List<Order> orders, Long userId) {
        Set<String> favoriteChefIds = getUserFavoriteChefIds(userId);

        return orders.stream()
                .map(order -> {
                    OrderDTO dto = convertToDTO(order);
                    dto.setIsFavorited(favoriteChefIds.contains(order.getChefId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取用户收藏的厨师 ID 列表。
     */
    private Set<String> getUserFavoriteChefIds(Long userId) {
        QueryWrapper<CustomerFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_id", userId);
        List<CustomerFavorite> favorites = customerFavoriteMapper.selectList(queryWrapper);

        return favorites.stream()
                .map(CustomerFavorite::getChefId)
                .collect(Collectors.toSet());
    }

    /**
     * 转换订单 DTO。
     */
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setChefId(order.getChefId());
        dto.setAddressId(order.getAddressId());
        dto.setReserveDate(order.getReserveDate());
        dto.setReserveTime(order.getReserveTime());
        dto.setDishRequirements(order.getDishRequirements());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setRemark(order.getRemark());
        dto.setCreateTime(order.getCreateTime());

        if (order.getChefId() != null) {
            QueryWrapper<ChefInfo> chefQueryWrapper = new QueryWrapper<>();
            chefQueryWrapper.eq("user_id", order.getChefId());
            ChefInfo chef = chefInfoMapper.selectOne(chefQueryWrapper);
            if (chef != null) {
                dto.setChefName(chef.getRealName());
                dto.setChefAvatar(chef.getAvatarUrl());
                dto.setChefLevel(chef.getChefLevel());
            }
        }

        return dto;
    }

    @Override
    public void cancelOrder(Long userId, Long orderId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!userId.equals(order.getCustomerId())) {
            throw new BusinessException("无权操作该订单");
        }
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            throw new BusinessException("该订单状态不可取消");
        }

        order.setStatus(4);
        orderMapper.updateById(order);

        incrementCompletedOrders(userId);

        log.info("用户 {} 取消订单 {}, 原因: {}", userId, orderId, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!userId.equals(order.getCustomerId())) {
            throw new BusinessException("无权操作该订单");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("该订单状态不可支付");
        }

        order.setStatus(2);
        order.setPaymentTime(LocalDateTime.now());
        int updateRows = orderMapper.updateById(order);
        if (updateRows <= 0) {
            throw new BusinessException("更新订单状态失败");
        }

        Integer pointsToAdd = order.getTotalAmount().intValue();
        addPointsToUser(userId, pointsToAdd);
        updateCustomerTotalSpent(userId, order.getTotalAmount());
        checkAndUpdateMemberLevel(userId);

        log.info("用户 {} 支付订单 {}, 金额: {}, 获得积分: {}", userId, orderId, order.getTotalAmount(), pointsToAdd);
    }

    @Override
    public void refundOrder(Long userId, Long orderId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!userId.equals(order.getCustomerId())) {
            throw new BusinessException("无权操作该订单");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("该订单状态不可申请退款");
        }

        order.setStatus(5);
        order.setRemark(reason);
        orderMapper.updateById(order);

        log.info("用户 {} 申请退款订单 {}, 原因: {}", userId, orderId, reason);
    }

    /**
     * 给用户 completed_orders +1。
     */
    private void incrementCompletedOrders(Long userId) {
        QueryWrapper<CustomerInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        CustomerInfo customerInfo = customerInfoMapper.selectOne(queryWrapper);

        if (customerInfo == null) {
            log.warn("用户信息不存在，无法增加完成订单数 userId={}", userId);
            return;
        }

        Integer currentCompleted = customerInfo.getCompletedOrders();
        if (currentCompleted == null) {
            currentCompleted = 0;
        }

        customerInfo.setCompletedOrders(currentCompleted + 1);
        customerInfo.setUpdateTime(LocalDateTime.now());

        int rows = customerInfoMapper.updateById(customerInfo);
        if (rows <= 0) {
            throw new BusinessException("更新用户完成订单数失败");
        }

        log.info("用户完成订单数已更新 userId={}, oldCompleted={}, newCompleted={}",
                userId, currentCompleted, currentCompleted + 1);
    }

    /**
     * 给用户增加积分。
     */
    private void addPointsToUser(Long userId, Integer points) {
        QueryWrapper<CustomerInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        CustomerInfo customerInfo = customerInfoMapper.selectOne(queryWrapper);

        if (customerInfo == null) {
            log.warn("用户信息不存在，无法增加积分 userId={}", userId);
            return;
        }

        Integer currentPoints = customerInfo.getPoints();
        Integer newPoints = currentPoints + points;
        customerInfo.setPoints(newPoints);
        customerInfo.setUpdateTime(LocalDateTime.now());

        int rows = customerInfoMapper.updateById(customerInfo);
        if (rows <= 0) {
            throw new BusinessException("更新用户积分失败");
        }

        log.info("用户积分已更新 userId={}, oldPoints={}, newPoints={}, addedPoints={}",
                userId, currentPoints, newPoints, points);
    }

    /**
     * 更新用户累计消费金额。
     */
    private void updateCustomerTotalSpent(Long userId, BigDecimal amount) {
        QueryWrapper<CustomerInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        CustomerInfo customerInfo = customerInfoMapper.selectOne(queryWrapper);

        if (customerInfo == null) {
            log.warn("用户信息不存在，无法更新消费金额 userId={}", userId);
            return;
        }

        BigDecimal currentTotal = customerInfo.getTotalSpent();
        if (currentTotal == null) {
            currentTotal = BigDecimal.ZERO;
        }

        BigDecimal newTotal = currentTotal.add(amount);
        customerInfo.setTotalSpent(newTotal);
        customerInfo.setUpdateTime(LocalDateTime.now());

        int rows = customerInfoMapper.updateById(customerInfo);
        if (rows <= 0) {
            throw new BusinessException("更新用户消费金额失败");
        }

        log.info("用户消费金额已更新 userId={}, oldTotal={}, newTotal={}, addedAmount={}",
                userId, currentTotal, newTotal, amount);
    }

    /**
     * 检查并升级会员等级。
     */
    private void checkAndUpdateMemberLevel(Long userId) {
        QueryWrapper<CustomerInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        CustomerInfo customerInfo = customerInfoMapper.selectOne(queryWrapper);

        if (customerInfo == null) {
            log.warn("用户信息不存在，无法检查会员等级 userId={}", userId);
            return;
        }

        Integer currentPoints = customerInfo.getPoints();
        Integer currentLevel = customerInfo.getMemberLevel();

        java.util.Map<Integer, Integer> levelConfig = new java.util.HashMap<>();
        levelConfig.put(1, 1000);
        levelConfig.put(2, 3000);
        levelConfig.put(3, 5000);
        levelConfig.put(4, 10000);

        Integer newLevel = currentLevel;

        for (int level = currentLevel + 1; level <= 4; level++) {
            Integer requiredPoints = levelConfig.get(level);
            if (requiredPoints != null && currentPoints >= requiredPoints) {
                newLevel = level;
            } else {
                break;
            }
        }

        if (newLevel > currentLevel) {
            Integer pointsToDeduct = levelConfig.get(newLevel);
            Integer remainingPoints = currentPoints - pointsToDeduct;

            customerInfo.setMemberLevel(newLevel);
            customerInfo.setPoints(Math.max(remainingPoints, 0));
            customerInfo.setUpdateTime(LocalDateTime.now());

            int rows = customerInfoMapper.updateById(customerInfo);
            if (rows <= 0) {
                throw new BusinessException("会员升级更新失败");
            }

            log.info("会员升级成功 userId={}, oldLevel={}, newLevel={}, usedPoints={}, remainingPoints={}",
                    userId, currentLevel, newLevel, pointsToDeduct, remainingPoints);
        }
    }
}
