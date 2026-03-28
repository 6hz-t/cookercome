package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.dto.request.OrderCreateRequest;
import com.hs.backend.dto.response.UserScheduleResponse;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.Order;
import com.hs.backend.entity.UserAddress;
import com.hs.backend.mapper.ChefInfoMapper;
import com.hs.backend.mapper.CustomerInfoMapper;
import com.hs.backend.mapper.OrderMapper;
import com.hs.backend.mapper.UserAddressMapper;
import com.hs.backend.service.CustomerOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户订单服务实现
 */
@Slf4j
@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private ChefInfoMapper chefInfoMapper;

    @Resource
    private CustomerInfoMapper customerInfoMapper;

    @Resource
    private UserAddressMapper userAddressMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(Long userId, OrderCreateRequest request) {
        try {
            QueryWrapper<ChefInfo> chefQueryWrapper = new QueryWrapper<>();
            chefQueryWrapper.eq("user_id", request.getChefId());
            ChefInfo chef = chefInfoMapper.selectOne(chefQueryWrapper);
            if (chef == null || chef.getStatus() != 1) {
                throw new BusinessException("厨师不存在或已停用");
            }

            LocalDate reserveDate = LocalDate.parse(request.getReserveDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            if (reserveDate.isBefore(LocalDate.now())) {
                throw new BusinessException("预约日期不能早于今天");
            }

            checkTimeSlotConflict(userId, request.getChefId(), request.getReserveDate(), request.getReserveTime());

            UserAddress address = getAddressFromDatabase(request.getAddressId(), userId);
            if (address == null) {
                throw new BusinessException("地址不存在");
            }

            if (request.getTotalAmount().compareTo(chef.getMinPrice()) < 0) {
                throw new BusinessException("订单金额不能低于厨师最低服务费：" + chef.getMinPrice() + "元");
            }

            String orderNo = generateOrderNo();

            Order order = new Order();
            order.setOrderNo(orderNo);
            order.setCustomerId(userId);
            order.setChefId(request.getChefId());
            order.setAddressId(request.getAddressId());

            LocalDateTime reserveDateTime = LocalDateTime.of(reserveDate, LocalTime.of(0, 0));
            order.setReserveDate(reserveDateTime);
            order.setReserveTime(request.getReserveTime());

            order.setDishRequirements(request.getDishRequirements());
            order.setTotalAmount(request.getTotalAmount());
            order.setStatus(0);
            order.setRemark(request.getRemark());

            int insert = orderMapper.insert(order);
            if (insert <= 0) {
                throw new BusinessException("创建订单失败");
            }

            incrementTotalOrders(userId);

            log.info("用户 {} 创建订单成功，订单号: {}, 厨师ID: {}", userId, orderNo, request.getChefId());
            return orderNo;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建订单失败", e);
            throw new BusinessException("创建订单失败：" + e.getMessage());
        }
    }

    /**
     * 检查时间段是否有冲突。
     */
    private void checkTimeSlotConflict(Long userId, String chefId, String reserveDate, String reserveTime) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chef_id", chefId)
                .eq("reserve_date", reserveDate)
                .eq("reserve_time", reserveTime)
                .in("status", 0, 1, 2, 3);

        List<Order> existingOrders = orderMapper.selectList(queryWrapper);

        if (!existingOrders.isEmpty()) {
            boolean isSameUser = existingOrders.stream().anyMatch(order -> userId.equals(order.getCustomerId()));

            if (isSameUser) {
                throw new BusinessException("当前时段已有预约订单，请选择其他时间");
            } else {
                throw new BusinessException("该时段已被预约，请选择其他时间");
            }
        }
    }

    /**
     * 给用户 total_orders +1。
     */
    private void incrementTotalOrders(Long userId) {
        QueryWrapper<CustomerInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        CustomerInfo customerInfo = customerInfoMapper.selectOne(queryWrapper);

        if (customerInfo == null) {
            log.warn("用户信息不存在，无法增加总订单数 userId={}", userId);
            return;
        }

        Integer currentTotal = customerInfo.getTotalOrders();
        if (currentTotal == null) {
            currentTotal = 0;
        }

        customerInfo.setTotalOrders(currentTotal + 1);
        customerInfo.setUpdateTime(LocalDateTime.now());

        int rows = customerInfoMapper.updateById(customerInfo);
        if (rows <= 0) {
            throw new BusinessException("更新用户总订单数失败");
        }

        log.info("用户总订单数已更新 userId={}, oldTotal={}, newTotal={}",
                userId, currentTotal, currentTotal + 1);
    }

    /**
     * 从数据库获取并校验地址归属。
     */
    private UserAddress getAddressFromDatabase(Long addressId, Long userId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address != null && userId.equals(address.getUserId())) {
            return address;
        }
        return null;
    }

    /**
     * 生成订单号。
     */
    private String generateOrderNo() {
        LocalDateTime now = LocalDateTime.now();
        String timeStr = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) ((Math.random() * 9000) + 1000);
        return timeStr + random;
    }

    @Override
    public List<UserScheduleResponse> getUserSchedule(Long userId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_id", userId)
                .ge("reserve_date", startDate.toString())
                .le("reserve_date", endDate.toString());

        List<Order> orders = orderMapper.selectList(queryWrapper);

        Map<String, Map<String, Order>> dateSlotMap = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getReserveDate().toLocalDate().toString(),
                        Collectors.toMap(Order::getReserveTime, order -> order, (v1, v2) -> v1)
                ));

        List<UserScheduleResponse> responses = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            UserScheduleResponse response = new UserScheduleResponse();
            response.setDate(currentDate);

            List<UserScheduleResponse.TimeSlotStatus> timeSlots = new ArrayList<>();
            String dateStr = currentDate.toString();

            List<String> predefinedSlots = List.of(
                    "09:00-11:00",
                    "11:00-13:00",
                    "14:00-16:00",
                    "16:00-18:00",
                    "19:00-21:00"
            );

            for (String slot : predefinedSlots) {
                UserScheduleResponse.TimeSlotStatus slotStatus = new UserScheduleResponse.TimeSlotStatus();
                slotStatus.setTimeSlot(slot);

                Map<String, Order> dayOrders = dateSlotMap.get(dateStr);
                if (dayOrders != null && dayOrders.containsKey(slot)) {
                    Order order = dayOrders.get(slot);
                    Integer dbStatus = order.getStatus();

                    if (dbStatus == 4 || dbStatus == 6) {
                        slotStatus.setStatus(0);
                        slotStatus.setOrderId(null);
                    } else {
                        slotStatus.setStatus(dbStatus == 3 ? 4 : dbStatus + 1);
                        slotStatus.setOrderId(order.getId());
                    }
                } else {
                    slotStatus.setStatus(0);
                    slotStatus.setOrderId(null);
                }

                timeSlots.add(slotStatus);
            }

            response.setTimeSlots(timeSlots);
            responses.add(response);
            currentDate = currentDate.plusDays(1);
        }

        return responses;
    }
}
