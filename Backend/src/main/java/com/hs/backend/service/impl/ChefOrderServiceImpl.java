package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.dto.OrderDTO;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.Order;
import com.hs.backend.entity.User;
import com.hs.backend.entity.UserAddress;
import com.hs.backend.mapper.ChefInfoMapper;
import com.hs.backend.mapper.CustomerInfoMapper;
import com.hs.backend.mapper.OrderMapper;
import com.hs.backend.mapper.UserAddressMapper;
import com.hs.backend.mapper.UserMapper;
import com.hs.backend.service.ChefOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Chef-side order service implementation.
 */
@Slf4j
@Service
public class ChefOrderServiceImpl implements ChefOrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private ChefInfoMapper chefInfoMapper;

    @Resource
    private CustomerInfoMapper customerInfoMapper;

    @Resource
    private UserAddressMapper userAddressMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public List<OrderDTO> getHistoryOrders(Long chefId) {
        requireChefId(chefId);
        List<String> orderChefIds = resolveOrderChefIds(chefId);
        List<Integer> statusList = List.of(3, 4);
        List<Order> orders = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .in(Order::getChefId, orderChefIds)
                .in(Order::getStatus, statusList)
                .orderByDesc(Order::getCreateTime));
        return convertToDTOs(orders);
    }

    @Override
    public List<OrderDTO> getPendingOrders(Long chefId) {
        requireChefId(chefId);
        List<String> orderChefIds = resolveOrderChefIds(chefId);
        // Keep both status 0/1 for compatibility with existing data.
        List<Integer> statusList = List.of(0, 1);
        List<Order> orders = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .in(Order::getChefId, orderChefIds)
                .in(Order::getStatus, statusList)
                .orderByDesc(Order::getCreateTime));
        return convertToDTOs(orders);
    }

    @Override
    public List<OrderDTO> getServingOrders(Long chefId) {
        requireChefId(chefId);
        List<String> orderChefIds = resolveOrderChefIds(chefId);
        List<Integer> statusList = List.of(2);
        List<Order> orders = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .in(Order::getChefId, orderChefIds)
                .in(Order::getStatus, statusList)
                .orderByDesc(Order::getCreateTime));
        return convertToDTOs(orders);
    }

    @Override
    public Map<String, Object> getTodayOrderSummary(Long chefId) {
        requireChefId(chefId);
        List<String> orderChefIds = resolveOrderChefIds(chefId);

        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        List<Order> todayOrders = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .in(Order::getChefId, orderChefIds)
                .ge(Order::getCreateTime, start)
                .lt(Order::getCreateTime, end));

        long todayOrder = todayOrders.stream()
                .filter(order -> order.getStatus() != null && order.getStatus() >= 2 && order.getStatus() <= 3)
                .count();

        long waitOrder = todayOrders.stream()
                .filter(order -> Objects.equals(order.getStatus(), 2) )
                .count();


        BigDecimal todayIncome = todayOrders.stream()
                .filter(order -> Objects.equals(order.getStatus(), 3))
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("Today order: {}, wait order: {}, income: {}", todayOrder, waitOrder, todayIncome);
        Map<String, Object> result = new HashMap<>();
        result.put("todayOrder", todayOrder);
        result.put("waitOrder", waitOrder);
        result.put("todayIncome", todayIncome);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptOrder(Long orderId, Long chefId) {
        requireChefId(chefId);
        List<String> orderChefIds = resolveOrderChefIds(chefId);
        if (orderId == null) {
            throw new BusinessException("orderId is required");
        }

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("Order not found");
        }
        
        String orderChefId = order.getChefId();
        if (orderChefId == null || !orderChefIds.contains(orderChefId)) {
            throw new BusinessException("Order does not belong to current chef");
        }
        if (!Objects.equals(order.getStatus(), 0) && !Objects.equals(order.getStatus(), 1)) {
            throw new BusinessException("Current order status does not allow accepting");
        }

        order.setStatus(1);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(Long orderId, Integer status) {
        if (orderId == null) {
            throw new BusinessException("orderId is required");
        }
        if (status == null || status < 2 || status > 4) {
            throw new BusinessException("Invalid order status");
        }

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("Order not found");
        }

        order.setStatus(status);
        // Status 3 means service completed, set service end time
        if (Objects.equals(status, 3)) {
            order.setServiceEndTime(LocalDateTime.now());
        }

        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChefStatus(Long chefId, Integer status) {
        requireChefId(chefId);

        ChefInfo chefInfo = chefInfoMapper.selectOne(new LambdaQueryWrapper<ChefInfo>()
                .eq(ChefInfo::getUserId, String.valueOf(chefId))
                .last("LIMIT 1"));

        if (chefInfo == null) {
            chefInfo = new ChefInfo();
            chefInfo.setUserId(String.valueOf(chefId));
            chefInfo.setAuditStatus(0);
            chefInfo.setStatus(1);
            chefInfoMapper.insert(chefInfo);
        }

        int targetStatus = (status != null && status > 0) ? 1 : 0;
        chefInfo.setStatus(targetStatus);
        chefInfoMapper.updateById(chefInfo);
    }

    private void requireChefId(Long chefId) {
        if (chefId == null) {
            throw new BusinessException("chefId is required");
        }
    }

    /**
     * Resolve all possible order-side chef ids for one logged-in chef user.
     * Compatibility:
     * 1) historical data may save order.chef_id as user id;
     * 2) current booking flow saves order.chef_id as t_chef_info.id.
     * 
     * @return List of String chef IDs (matching order.chef_id type)
     */
    private List<String> resolveOrderChefIds(Long chefUserId) {
        Set<String> ids = new LinkedHashSet<>();
        // Add user ID as string (for legacy compatibility)
        ids.add(String.valueOf(chefUserId));

        List<ChefInfo> chefInfos = chefInfoMapper.selectList(new LambdaQueryWrapper<ChefInfo>()
                .eq(ChefInfo::getUserId, String.valueOf(chefUserId)));

        for (ChefInfo chefInfo : chefInfos) {
            if (chefInfo != null && chefInfo.getId() != null) {
                // Add chef info ID as string (matching order.chef_id format)
                ids.add(String.valueOf(chefInfo.getId()));
            }
        }

        return new ArrayList<>(ids);
    }

    private List<OrderDTO> convertToDTOs(List<Order> orders) {
        List<OrderDTO> dtos = new ArrayList<>();
        for (Order order : orders) {
            dtos.add(convertToDTO(order));
        }
        return dtos;
    }

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
        dto.setPaymentTime(order.getPaymentTime());
        dto.setServiceStartTime(order.getServiceStartTime());
        dto.setServiceEndTime(order.getServiceEndTime());

        if (order.getCustomerId() != null) {
            CustomerInfo customerInfo = customerInfoMapper.selectOne(new LambdaQueryWrapper<CustomerInfo>()
                    .eq(CustomerInfo::getUserId, order.getCustomerId())
                    .last("LIMIT 1"));
            if (customerInfo != null) {
                dto.setCustomerName(customerInfo.getUsername());
            }

            User user = userMapper.selectById(order.getCustomerId());
            if (user != null) {
                dto.setCustomerPhone(user.getPhone());
            }
        }

        if (order.getAddressId() != null) {
            UserAddress address = userAddressMapper.selectById(order.getAddressId());
            if (address != null) {
                dto.setAddress(buildFullAddress(address));
            }
        }

        return dto;
    }

    private String buildFullAddress(UserAddress address) {
        StringBuilder builder = new StringBuilder();
        if (address.getProvince() != null) {
            builder.append(address.getProvince());
        }
        if (address.getCity() != null) {
            builder.append(address.getCity());
        }
        if (address.getDistrict() != null) {
            builder.append(address.getDistrict());
        }
        if (address.getDetailAddress() != null) {
            builder.append(address.getDetailAddress());
        }
        return builder.toString();
    }
}
