package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.Order;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.entity.User;
import com.hs.backend.mapper.CustomerInfoMapper;
import com.hs.backend.mapper.ChefInfoMapper;
import com.hs.backend.mapper.OrderMapper;
import com.hs.backend.mapper.UserMapper;
import com.hs.backend.service.OrderService;
import com.hs.backend.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private ChefInfoMapper chefInfoMapper;

    @Override
    public Page<OrderDetailVO> getOrderPage(Integer page, Integer size, String keyword, Integer status, String startDate, String endDate) {
        Page<Order> ordersPage = new Page<>(page, size);

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（订单号或客户 ID）
        if (StringUtils.hasText(keyword)) {
            try {
                Long customerId = Long.parseLong(keyword);
                wrapper.and(w -> w
                    .like(Order::getOrderNo, keyword)
                    .or()
                    .eq(Order::getCustomerId, customerId)
                );
            } catch (NumberFormatException e) {
                wrapper.like(Order::getOrderNo, keyword);
            }
        }

        // 状态筛选
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        // 时间范围筛选
        if (StringUtils.hasText(startDate)) {
            try {
                LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
                wrapper.ge(Order::getCreateTime, startDateTime);
            } catch (Exception e) {
                // 日期格式错误，忽略时间筛选
            }
        }
        if (StringUtils.hasText(endDate)) {
            try {
                LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(LocalTime.MAX);
                wrapper.le(Order::getCreateTime, endDateTime);
            } catch (Exception e) {
                // 日期格式错误，忽略时间筛选
            }
        }

        // 按创建时间倒序
        wrapper.orderByDesc(Order::getCreateTime);

        // 执行分页查询
        Page<Order> resultPage = page(ordersPage, wrapper);

        // 转换为 OrderDetailVO
        Page<OrderDetailVO> voPage = new Page<>(page, size);
        List<OrderDetailVO> voList = resultPage.getRecords().stream().map(order -> {
            OrderDetailVO vo = new OrderDetailVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setCustomerId(order.getCustomerId());
            vo.setChefId(order.getChefId());
            vo.setAppointmentTime(order.getReserveDate() != null ? order.getReserveDate().toString() + " " + order.getReserveTime() : order.getReserveTime());
            vo.setStatus(order.getStatus());
            vo.setTotalFee(order.getTotalAmount());
            vo.setCreateTime(order.getCreateTime());

            try {
                // 获取客户信息
                CustomerInfo customerInfo = customerInfoMapper.selectOne(
                    new LambdaQueryWrapper<CustomerInfo>().eq(CustomerInfo::getUserId, order.getCustomerId())
                );
                if (customerInfo != null && customerInfo.getRealName() != null) {
                    vo.setCustomerName(customerInfo.getRealName());
                }
                // 获取客户手机号（从 User 表）
                User customerUser = userMapper.selectById(order.getCustomerId());
                if (customerUser != null) {
                    vo.setCustomerPhone(customerUser.getPhone());
                }
            } catch (Exception e) {
                // 忽略客户信息获取失败
            }

            try {
                // 获取厨师信息
                ChefInfo chefInfo = chefInfoMapper.selectOne(
                    new LambdaQueryWrapper<ChefInfo>().eq(ChefInfo::getUserId, order.getChefId())
                );
                if (chefInfo != null && chefInfo.getRealName() != null) {
                    vo.setChefName(chefInfo.getRealName());
                }
            } catch (Exception e) {
                // 忽略厨师信息获取失败
            }

            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        voPage.setTotal(resultPage.getTotal());
        voPage.setSize(resultPage.getSize());
        voPage.setCurrent(resultPage.getCurrent());

        return voPage;
    }

    @Override
    public boolean forceCancelOrder(Long orderId, String reason) {
        Order order = getById(orderId);
        if (order == null) {
            return false;
        }

        // 更新订单状态为已取消
        order.setStatus(5); // 5-已取消
        order.setRemark(reason); // 使用备注字段存储取消原因
        order.setUpdateTime(LocalDateTime.now());

        return updateById(order);
    }
}
