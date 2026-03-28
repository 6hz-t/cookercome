package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.dto.OrderDTO;
import com.hs.backend.dto.request.OrderActionRequest;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 客户订单查询服务实现类
 */
@Slf4j
@Service
public class CustomerOrderQueryServiceImpl implements CustomerOrderQueryService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private ChefInfoMapper chefInfoMapper;

    @Resource
    private CustomerInfoMapper customerInfoMapper;

    @Resource
    private CustomerFavoriteMapper customerFavoriteMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String ORDER_CACHE_PREFIX = "user:orders:";
    private static final long ORDER_CACHE_EXPIRE_MINUTES = 10;

    @Override
    public List<OrderDTO> getUserOrders(Long userId, String category) {
        // 构建缓存 key
        String cacheKey = buildCacheKey(userId, category);
        
        // 尝试从 Redis 获取
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof List) {
            log.debug("从 Redis 缓存中获取到用户 {} 的 {} 订单", userId, category);
            return (List<OrderDTO>) cached;
        }

        // 从数据库查询
        log.debug("从数据库查询用户 {} 的 {} 订单", userId, category);
        List<Order> orders = queryOrdersFromDB(userId, category);
        
        // 转换为 DTO（带收藏标记）
        List<OrderDTO> orderDTOs = convertToDTOs(orders, userId);
        
        // 存入 Redis 缓存
        if (!orderDTOs.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, orderDTOs, ORDER_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            log.debug("订单数据已存入 Redis 缓存，key: {}", cacheKey);
        }

        return orderDTOs;
    }

    /**
     * 构建缓存 key
     */
    private String buildCacheKey(Long userId, String category) {
        return ORDER_CACHE_PREFIX + category + ":" + userId;
    }

    /**
     * 从数据库查询订单
     */
    private List<Order> queryOrdersFromDB(Long userId, String category) {
        List<Integer> statusList = getStatusListByCategory(category);
        
        if (statusList == null || statusList.isEmpty()) {
            return new ArrayList<>();
        }

        // 使用 MyBatis Plus 查询
        return orderMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                .eq(Order::getCustomerId, userId)
                .in(Order::getStatus, statusList)
                .orderByDesc(Order::getCreateTime));
    }

    /**
     * 根据分类获取状态列表
     */
    private List<Integer> getStatusListByCategory(String category) {
        switch (category) {
            case "all":
                // 全部订单：0-订单提交 1-待支付，2-已支付，3-服务完成，4-订单取消，5-退款中，6-已退款
                return List.of(0, 1, 2, 3, 4, 5, 6);
            case "pending":
                // 预约中：0
                return List.of(0);
            case "payment":
                // 待支付：1
                return List.of(1);
            case "fulfillment":
                // 待履约：2
                return List.of(2);
            case "refunding":
                // 退款中：5
                return List.of(5);
            case "history":
                // 历史订单：3, 4, 6
                return List.of(3, 4, 6);
            default:
                // 默认返回全部进行中的订单
                return List.of(0, 1, 2);
        }
    }

    /**
     * 转换为 DTO（带收藏标记）
     */
    private List<OrderDTO> convertToDTOs(List<Order> orders, Long userId) {
        // 批量查询用户的收藏列表
        Set<String> favoriteChefIds = getUserFavoriteChefIds(userId);
        
        return orders.stream()
                .map(order -> {
                    OrderDTO dto = convertToDTO(order);
                    // 设置收藏标记（order.getChefId() 存储的是 chefinfo 表的 user_id）
                    dto.setIsFavorited(favoriteChefIds.contains(order.getChefId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取用户收藏的厨师 ID 列表（从 Redis 缓存）
     */
    private Set<String> getUserFavoriteChefIds(Long userId) {
        String cacheKey = "user:favorites:" + userId;
        
        // 尝试从 Redis 获取
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof Set) {
            log.debug("从 Redis 缓存中获取到用户 {} 的收藏列表", userId);
            return (Set<String>) cached;
        }
        
        // 从数据库查询
        log.debug("从数据库查询用户 {} 的收藏列表", userId);
        QueryWrapper<CustomerFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_id", userId);
        List<CustomerFavorite> favorites = customerFavoriteMapper.selectList(queryWrapper);
        
        // 转换为 Set
        Set<String> chefIds = favorites.stream()
                .map(CustomerFavorite::getChefId)
                .collect(Collectors.toSet());
        
        // 存入 Redis 缓存（30 分钟）
        if (!chefIds.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, chefIds, 30, TimeUnit.MINUTES);
            log.debug("收藏列表已存入 Redis 缓存，key: {}", cacheKey);
        }
        
        return chefIds;
    }

    /**
     * 转换为 DTO
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
        
        // 查询厨师信息（order.getChefId() 存储的是 chefinfo 表的 user_id）
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
            throw new BusinessException("该订单状态不能取消");
        }

        // 更新订单状态为已取消（4）
        order.setStatus(4);
        orderMapper.updateById(order);

        // 给用户 completed_orders 加 1（取消的订单也算完成订单）
        incrementCompletedOrders(userId);

        // 清除用户信息缓存（completed_orders 已更新，需要刷新缓存）
        clearUserInfoCache(userId);

        // 清除所有订单缓存
        clearAllUserOrdersCache(userId);
        log.info("用户 {} 取消了订单 {}, 原因：{}", userId, orderId, reason);
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
            throw new BusinessException("该订单状态不能支付");
        }

        // 1. 更新订单状态为已支付（2），记录支付时间
        order.setStatus(2);
        order.setPaymentTime(LocalDateTime.now());
        int updateRows = orderMapper.updateById(order);
        if (updateRows <= 0) {
            throw new BusinessException("更新订单状态失败");
        }

        // 2. 给用户增加积分（1 元=1 积分）
        Integer pointsToAdd = order.getTotalAmount().intValue();
        addPointsToUser(userId, pointsToAdd);

        // 3. 更新用户累计消费金额
        updateCustomerTotalSpent(userId, order.getTotalAmount());

        // 4. 检查并升级会员等级
        checkAndUpdateMemberLevel(userId);

        // 5. 清除所有订单缓存
        clearAllUserOrdersCache(userId);
        
        // 6. 清除用户的时间段缓存
        clearUserScheduleCache(userId);
        
        // 7. 清除用户信息缓存（积分、消费金额已更新，需要刷新缓存）
        clearUserInfoCache(userId);
        
        log.info("用户 {} 支付了订单 {}, 金额：{}, 获得积分：{}", userId, orderId, order.getTotalAmount(), pointsToAdd);
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
            throw new BusinessException("该订单状态不能申请退款");
        }

        // 更新订单状态为退款中（5）
        order.setStatus(5);
        order.setRemark(reason); // 保存退款原因到备注
        orderMapper.updateById(order);

        // 清除所有订单缓存
        clearAllUserOrdersCache(userId);
        
        // 清除用户的时间段缓存
        clearUserScheduleCache(userId);
        
        log.info("用户 {} 申请退款订单 {}, 原因：{}", userId, orderId, reason);
    }

    /**
     * 给用户 completed_orders 加 1
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
     * 清除用户的所有订单缓存
     */
    private void clearAllUserOrdersCache(Long userId) {
        // 清除所有分类的缓存
        List<String> categories = List.of("all", "pending", "payment", "fulfillment", "refunding", "history");
        for (String category : categories) {
            String cacheKey = ORDER_CACHE_PREFIX + category + ":" + userId;
            redisTemplate.delete(cacheKey);
            log.debug("已清除用户 {} 的 {} 订单缓存", userId, category);
        }
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
     * 清除用户时间段缓存
     */
    private void clearUserScheduleCache(Long userId) {
        // 删除该用户所有日期范围的缓存（使用通配符模式）
        String pattern = "user:schedule:" + userId + ":*";
        try {
            redisTemplate.keys(pattern).forEach(key -> {
                redisTemplate.delete(key);
                log.debug("已清除用户 {} 的时间段缓存 key={}", userId, key);
            });
        } catch (Exception e) {
            log.warn("清除时间段缓存失败：{}", e.getMessage());
        }
    }

    /**
     * 给用户增加积分
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
     * 更新用户累计消费金额
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
     * 检查并升级会员等级
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
        
        // 会员等级配置：等级 -> 所需积分
        java.util.Map<Integer, Integer> levelConfig = new java.util.HashMap<>();
        levelConfig.put(1, 1000);  // 白银会员：1000 积分
        levelConfig.put(2, 3000);  // 黄金会员：3000 积分
        levelConfig.put(3, 5000);  // 铂金会员：5000 积分
        levelConfig.put(4, 10000); // 钻石会员：10000 积分
        
        Integer newLevel = currentLevel;
        
        // 检查是否可以升级到更高等级
        for (int level = currentLevel + 1; level <= 4; level++) {
            Integer requiredPoints = levelConfig.get(level);
            if (requiredPoints != null && currentPoints >= requiredPoints) {
                newLevel = level;
            } else {
                break;
            }
        }
        
        // 如果等级提升，更新等级并扣除积分
        if (newLevel > currentLevel) {
            Integer pointsToDeduct = levelConfig.get(newLevel);
            Integer remainingPoints = currentPoints - pointsToDeduct;
            
            // 更新会员等级和剩余积分
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
