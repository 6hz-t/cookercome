package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hs.backend.dto.request.OrderCreateRequest;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.Order;
import com.hs.backend.entity.UserAddress;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.mapper.ChefInfoMapper;
import com.hs.backend.mapper.CustomerInfoMapper;
import com.hs.backend.mapper.OrderMapper;
import com.hs.backend.mapper.UserAddressMapper;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private RedisTemplate<String, Object> redisTemplate;

    private static final String ADDRESS_CACHE_PREFIX = "user:address:";
    private static final long ADDRESS_CACHE_EXPIRE_MINUTES = 30;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(Long userId, OrderCreateRequest request) {
        try {
            // 1. 验证厨师是否存在
            ChefInfo chef = chefInfoMapper.selectById(request.getChefId());
            if (chef == null || chef.getStatus() != 1) {
                throw new BusinessException("厨师不存在或已停用");
            }

            // 2. 验证预约日期不能早于今天
            LocalDate reserveDate = LocalDate.parse(request.getReserveDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            if (reserveDate.isBefore(LocalDate.now())) {
                throw new BusinessException("预约日期不能早于今天");
            }

            // 3. 从 Redis 获取地址，如果没有则从数据库获取并存入 Redis
            UserAddress address = getAddressFromCache(request.getAddressId(), userId);
            if (address == null) {
                throw new BusinessException("地址不存在");
            }

            // 4. 验证订单金额不能小于厨师最少金额
            if (request.getTotalAmount().compareTo(chef.getMinPrice()) < 0) {
                throw new BusinessException("订单金额不能低于厨师最低服务费：" + chef.getMinPrice() + "元");
            }

            // 5. 生成订单号（格式：年月日时分秒 + 随机数）
            String orderNo = generateOrderNo();

            // 6. 创建订单
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

            // 7. 给用户 total_orders 加 1
            incrementTotalOrders(userId);

            // 8. 清除用户信息缓存（total_orders 已更新，需要刷新缓存）
            clearUserInfoCache(userId);

            // 清除 Redis 中该用户的所有订单缓存（all/pending/payment/fulfillment/history）
            clearAllUserOrdersCache(userId);

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
}
