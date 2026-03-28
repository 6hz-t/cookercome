package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hs.backend.dto.request.OrderCreateRequest;
import com.hs.backend.dto.response.UserScheduleResponse;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.Order;
import com.hs.backend.entity.User;
import com.hs.backend.entity.UserAddress;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.mapper.ChefInfoMapper;
import com.hs.backend.mapper.CustomerInfoMapper;
import com.hs.backend.mapper.OrderMapper;
import com.hs.backend.mapper.UserAddressMapper;
import com.hs.backend.mapper.UserMapper;
import com.hs.backend.service.CustomerOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 客户订单服务实现类
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

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String ADDRESS_CACHE_PREFIX = "user:address:";
    private static final long ADDRESS_CACHE_EXPIRE_MINUTES = 30;
    
    // 用户时间段缓存 Key
    private static final String USER_SCHEDULE_CACHE_KEY = "user:schedule:";
    private static final long USER_SCHEDULE_CACHE_EXPIRE_HOURS = 2L;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(Long userId, OrderCreateRequest request) {
        try {
            // 1. 验证厨师是否存在（request.getChefId() 是 chefinfo 表的 user_id）
            QueryWrapper<ChefInfo> chefQueryWrapper = new QueryWrapper<>();
            chefQueryWrapper.eq("user_id", request.getChefId());
            ChefInfo chef = chefInfoMapper.selectOne(chefQueryWrapper);
            if (chef == null || chef.getStatus() != 1) {
                throw new BusinessException("厨师不存在或已停用");
            }

            // 3. 验证预约日期不能早于今天
            LocalDate reserveDate = LocalDate.parse(request.getReserveDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            if (reserveDate.isBefore(LocalDate.now())) {
                throw new BusinessException("预约日期不能早于今天");
            }

            // 4. 检查该厨师在该时间段是否已有预约（排除已取消/已完成等状态）
            checkTimeSlotConflict(userId, request.getChefId(), request.getReserveDate(), request.getReserveTime());

            // 5. 从 Redis 获取地址，如果没有则从数据库获取并存入 Redis
            UserAddress address = getAddressFromCache(request.getAddressId(), userId);
            if (address == null) {
                throw new BusinessException("地址不存在");
            }

            // 6. 验证订单金额不能小于厨师最少金额
            if (request.getTotalAmount().compareTo(chef.getMinPrice()) < 0) {
                throw new BusinessException("订单金额不能低于厨师最低服务费：" + chef.getMinPrice() + "元");
            }

            // 7. 生成订单号（格式：年月日时分秒 + 随机数）
            String orderNo = generateOrderNo();

            // 8. 创建订单
            Order order = new Order();
            order.setOrderNo(orderNo);
            order.setCustomerId(userId);
            order.setChefId(request.getChefId());
            order.setAddressId(request.getAddressId());
            
            // 设置预约日期和时间
            LocalDateTime reserveDateTime = LocalDateTime.of(reserveDate, LocalTime.of(0, 0));
            order.setReserveDate(reserveDateTime);
            order.setReserveTime(request.getReserveTime());
            
            order.setDishRequirements(request.getDishRequirements());
            order.setTotalAmount(request.getTotalAmount());
            order.setStatus(0); // 待支付
            order.setRemark(request.getRemark());

            int insert = orderMapper.insert(order);
            if (insert <= 0) {
                throw new BusinessException("创建订单失败");
            }

            // 9. 给用户 total_orders 加 1
            incrementTotalOrders(userId);

            // 10. 清除用户信息缓存（total_orders 已更新，需要刷新缓存）
            clearUserInfoCache(userId);

            // 11. 清除 Redis 中该用户的所有订单缓存（all/pending/payment/fulfillment/history）
            clearAllUserOrdersCache(userId);
            
            // 12. 清除用户的时间段缓存
            clearUserScheduleCache(userId);

            log.info("用户 {} 创建订单成功，订单号：{}, 厨师 ID: {}", userId, orderNo, request.getChefId());
            return orderNo;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建订单失败", e);
            throw new BusinessException("创建订单失败：" + e.getMessage());
        }
    }

    /**
     * 检查时间段是否有冲突
     * @param userId 当前下单用户 ID
     * @param chefId 厨师 ID（chefinfo 表的 user_id）
     * @param reserveDate 预约日期
     * @param reserveTime 预约时间段（如：11:00-13:00）
     */
    private void checkTimeSlotConflict(Long userId, String chefId, String reserveDate, String reserveTime) {
        // 查询该厨师在该日期的所有订单
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chef_id", chefId)
                    .eq("reserve_date", reserveDate)
                    .eq("reserve_time", reserveTime)
                    // 只检查有效订单（排除已取消、已完成、已退款等状态）
                    // 订单状态：0-待支付，1-已支付，2-厨师接单，3-服务中，4-服务完成，5-订单取消，6-退款中，7-已退款
                    .in("status", 0, 1, 2, 3); // 只检查待支付、已支付、厨师接单、服务中的订单
        
        List<Order> existingOrders = orderMapper.selectList(queryWrapper);
        
        if (!existingOrders.isEmpty()) {
            log.warn("厨师 {} 在 {} {} 时间段已有预约订单", chefId, reserveDate, reserveTime);
            
            // 判断是否是同一个用户
            boolean isSameUser = existingOrders.stream().anyMatch(order -> userId.equals(order.getCustomerId()));
            
            if (isSameUser) {
                // 自己预约自己的时间段
                throw new BusinessException("当前时段已有预约订单，请选择其他时间");
            } else {
                // 自己预约别人已预约的时间段
                throw new BusinessException("该时段已被预约，请选择其他时间");
            }
        }
    }

    /**
     * 给用户 total_orders 加 1
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
     * 清除用户信息缓存
     */
    private void clearUserInfoCache(Long userId) {
        String cacheKey = "user:info:" + userId;
        redisTemplate.delete(cacheKey);
        log.debug("已清除用户信息缓存 key={}", cacheKey);
    }

    /**
     * 从缓存获取地址，如果缓存没有则从数据库获取并缓存
     */
    private UserAddress getAddressFromCache(Long addressId, Long userId) {
        String cacheKey = ADDRESS_CACHE_PREFIX + userId + ":" + addressId;
        
        // 先从 Redis 获取
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof UserAddress) {
            log.debug("从 Redis 缓存中获取到地址：{}", addressId);
            return (UserAddress) cached;
        }

        // 从数据库获取
        log.debug("从数据库中查询地址：{}", addressId);
        UserAddress address = userAddressMapper.selectById(addressId);
        
        if (address != null && userId.equals(address.getUserId())) {
            // 存入 Redis
            redisTemplate.opsForValue().set(cacheKey, address, ADDRESS_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            log.debug("地址已存入 Redis 缓存");
            return address;
        }

        return null;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        LocalDateTime now = LocalDateTime.now();
        String timeStr = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) ((Math.random() * 9000) + 1000);
        return timeStr + random;
    }

    /**
     * 清除用户的所有订单缓存
     */
    private void clearAllUserOrdersCache(Long userId) {
        // 清除所有分类的缓存
        List<String> categories = List.of("all", "pending", "payment", "fulfillment", "history");
        for (String category : categories) {
            String cacheKey = "user:orders:" + category + ":" + userId;
            redisTemplate.delete(cacheKey);
            log.debug("已清除用户 {} 的 {} 订单缓存", userId, category);
        }
    }
    
    /**
     * 清除用户时间段缓存
     */
    private void clearUserScheduleCache(Long userId) {
        // 删除该用户所有日期范围的缓存（使用通配符模式）
        String pattern = USER_SCHEDULE_CACHE_KEY + userId + ":*";
        try {
            redisTemplate.keys(pattern).forEach(key -> {
                redisTemplate.delete(key);
                log.debug("已清除用户 {} 的时间段缓存 key={}", userId, key);
            });
        } catch (Exception e) {
            log.warn("清除时间段缓存失败：{}", e.getMessage());
        }
    }
    
    @Override
    public List<UserScheduleResponse> getUserSchedule(Long userId, LocalDate startDate, LocalDate endDate) {
        // 构建缓存 key
        String cacheKey = USER_SCHEDULE_CACHE_KEY + userId + ":" + startDate + ":" + endDate;
        
        // 1. 先尝试从 Redis 获取缓存
        try {
            Object cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData instanceof List) {
                log.debug("从 Redis 缓存中获取到用户 {} 的时间段安排", userId);
                return (List<UserScheduleResponse>) cachedData;
            }
        } catch (Exception e) {
            log.warn("Redis 缓存读取失败，降级到数据库查询：{}", e.getMessage());
        }
        
        log.debug("未命中缓存，从数据库查询用户 {} 的时间段安排", userId);
        
        // 2. 缓存未命中，查询数据库
        // 查询该用户在指定日期范围内的所有订单
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_id", userId)
                    .ge("reserve_date", startDate.toString())
                    .le("reserve_date", endDate.toString());
        
        List<Order> orders = orderMapper.selectList(queryWrapper);
        
        // 3. 按日期和时间段分组
        // 注意：reserveDate 是 LocalDateTime，需要转为 LocalDate 字符串
        Map<String, Map<String, Order>> dateSlotMap = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getReserveDate().toLocalDate().toString(),  // 关键：先取日期部分再转字符串
                        Collectors.toMap(Order::getReserveTime, order -> order, (v1, v2) -> v1)
                ));
        
        // 4. 构建响应数据
        List<UserScheduleResponse> responses = new ArrayList<>();
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            UserScheduleResponse response = new UserScheduleResponse();
            response.setDate(currentDate);
            
            List<UserScheduleResponse.TimeSlotStatus> timeSlots = new ArrayList<>();
            String dateStr = currentDate.toString();
            
            // 定义 5 个时间段（可根据实际情况调整）
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
                    
                    // 状态映射逻辑：根据订单业务流程
                    // 数据库状态 4（取消订单）和 6（退款成功）按空闲处理
                    if (dbStatus == 4 || dbStatus == 6) {
                        slotStatus.setStatus(0); // 空闲
                        slotStatus.setOrderId(null);
                    } else {
                        // 其他状态直接映射到前端展示状态
                        // 数据库 0（待接单）-> 前端 1（待接单）
                        // 数据库 1（待支付）-> 前端 2（待支付）
                        // 数据库 2（已支付）-> 前端 3（已预约）
                        // 数据库 3（服务完成）-> 前端 4（服务中）
                        // 数据库 5（退款中）-> 前端 5（退款中）
                        slotStatus.setStatus(dbStatus == 3 ? 4 : dbStatus + 1);
                        slotStatus.setOrderId(order.getId());
                    }
                } else {
                    slotStatus.setStatus(0); // 空闲
                    slotStatus.setOrderId(null);
                }
                
                timeSlots.add(slotStatus);
            }
            
            response.setTimeSlots(timeSlots);
            responses.add(response);
            
            currentDate = currentDate.plusDays(1);
        }
        
        // 5. 将结果存入 Redis 缓存
        try {
            redisTemplate.opsForValue().set(cacheKey, responses, USER_SCHEDULE_CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
            log.debug("用户时间段数据已存入 Redis 缓存，key: {}, 有效期 {} 小时", cacheKey, USER_SCHEDULE_CACHE_EXPIRE_HOURS);
        } catch (Exception e) {
            log.warn("Redis 缓存写入失败：{}", e.getMessage());
        }
        
        return responses;
    }
}
