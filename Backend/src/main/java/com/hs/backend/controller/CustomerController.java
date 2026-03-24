package com.hs.backend.controller;

import com.hs.backend.common.Result;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.dto.request.OrderCreateRequest;
import com.hs.backend.dto.request.OrderActionRequest;
import com.hs.backend.dto.OrderDTO;
import com.hs.backend.dto.response.UserScheduleResponse;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.UserAddress;
import com.hs.backend.service.CustomerOrderService;
import com.hs.backend.service.CustomerOrderQueryService;
import com.hs.backend.service.CustomerPersonalCenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

/**
     * 客户控制器
     * 负责用户/客户相关的业务接口
 */
@Slf4j
@Tag(name = "客户管理", description = "客户个人信息、收货地址等客户相关接口")
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerPersonalCenterService personalCenterService;
    private final CustomerOrderService customerOrderService;
    private final CustomerOrderQueryService customerOrderQueryService;

   public CustomerController(CustomerPersonalCenterService personalCenterService, 
                            CustomerOrderService customerOrderService,
                            CustomerOrderQueryService customerOrderQueryService) {
        this.personalCenterService = personalCenterService;
        this.customerOrderService = customerOrderService;
        this.customerOrderQueryService = customerOrderQueryService;
    }

    /**
     * 从 Principal 中获取当前登录用户 ID
     */
    private Long getCurrentUserId(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new RuntimeException("未找到当前登录用户");
        }
        try {
            return Long.parseLong(principal.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("用户 ID 格式错误", e);
        }
    }

    /**
     * 获取当前客户个人信息
     */
    @GetMapping("/profile")
    @Operation(summary = "获取客户个人信息", description = "获取当前登录客户的个人信息")
   public Result<CustomerInfo> getProfile(Principal principal) {
        // 从 Principal 中获取当前登录用户 ID
        Long userId = getCurrentUserId(principal);
        CustomerInfo customerInfo = personalCenterService.getCustomerProfile(userId);
        return Result.success(customerInfo);
    }

    /**
     * 更新客户个人信息
     */
    @PutMapping("/profile")
    @Operation(summary = "更新客户个人信息", description = "更新当前登录客户的个人信息")
   public Result<String> updateProfile(@RequestBody CustomerInfo customerInfo, Principal principal) {
        // 从 Principal 中获取当前登录用户 ID
        Long userId = getCurrentUserId(principal);
        personalCenterService.updateCustomerProfile(userId, customerInfo);
        return Result.success("更新成功");
    }

    /**
     * 获取客户的收货地址列表
     */
    @GetMapping("/addresses")
    @Operation(summary = "获取收货地址列表", description = "获取当前登录客户的所有收货地址")
   public Result<List<UserAddress>> getAddresses(Principal principal) {
        // 从 Principal 中获取当前登录用户 ID
        Long userId = getCurrentUserId(principal);
        List<UserAddress> addresses = personalCenterService.getUserAddresses(userId);
        return Result.success(addresses);
    }

    /**
     * 添加收货地址
     */
    @PostMapping("/addresses")
    @Operation(summary = "添加收货地址", description = "为当前登录客户添加新的收货地址")
   public Result<String> addAddress(@RequestBody UserAddress address, Principal principal) {
        // 从 Principal 中获取当前登录用户 ID
        Long userId = getCurrentUserId(principal);
        personalCenterService.addUserAddress(userId, address);
        return Result.success("添加成功");
    }

    /**
     * 更新收货地址
     */
    @PutMapping("/addresses/{id}")
    @Operation(summary = "更新收货地址", description = "更新指定 ID 的收货地址信息")
   public Result<String> updateAddress(@PathVariable Long id, @RequestBody UserAddress address, Principal principal) {
        // 从 Principal 中获取当前登录用户 ID
        Long userId = getCurrentUserId(principal);
        personalCenterService.updateUserAddress(userId, address);
        return Result.success("更新成功");
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("/addresses/{id}")
    @Operation(summary = "删除收货地址", description = "删除指定 ID 的收货地址")
   public Result<String> deleteAddress(@PathVariable Long id, Principal principal) {
        // 从 Principal 中获取当前登录用户 ID
        Long userId = getCurrentUserId(principal);
        personalCenterService.deleteUserAddress(userId, id);
        return Result.success("删除成功");
    }

    /**
     * 获取个人中心统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取个人中心统计信息", description = "获取当前登录用户的订单、收藏、优惠券等统计信息")
   public Result<java.util.Map<String, Object>> getStats(Principal principal) {
        // 从 Principal 中获取当前登录用户 ID
        Long userId = getCurrentUserId(principal);
        java.util.Map<String, Object> stats = personalCenterService.getPersonalCenterStats(userId);
        return Result.success(stats);
    }

    /**
     * 创建订单
     */
    @PostMapping("/order/create")
    @Operation(summary = "创建订单", description = "客户预约厨师，创建新订单")
    public Result<String> createOrder(@Valid @RequestBody OrderCreateRequest request, Principal principal) {
        // 从 Principal 中获取当前登录用户 ID
        Long userId = getCurrentUserId(principal);
        String orderNo = customerOrderService.createOrder(userId, request);
        return Result.success(orderNo);
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/orders")
    @Operation(summary = "获取用户订单列表", description = "支持分类查询：all-全部 pending-预约中 payment-待支付 fulfillment-待履约 history-历史订单")
    public Result<List<OrderDTO>> getUserOrders(
            @RequestParam(defaultValue = "all") String category,
            Principal principal) {
        Long userId = getCurrentUserId(principal);
        List<OrderDTO> orders = customerOrderQueryService.getUserOrders(userId, category);
        return Result.success(orders);
    }

    /**
     * 操作订单（取消、支付、退款）
     */
    @PostMapping("/order/action")
    @Operation(summary = "操作订单", description = "操作类型：cancel-取消订单，pay-支付订单，refund-申请退款")
    public Result<String> actionOrder(@Valid @RequestBody OrderActionRequest request, Principal principal) {
        Long userId = getCurrentUserId(principal);
        
        switch (request.getActionType()) {
            case "cancel":
                customerOrderQueryService.cancelOrder(userId, request.getOrderId(), request.getReason());
                return Result.success("订单已取消");
            case "pay":
                customerOrderQueryService.payOrder(userId, request.getOrderId());
                return Result.success("订单已支付");
            case "refund":
                customerOrderQueryService.refundOrder(userId, request.getOrderId(), request.getReason());
                return Result.success("退款申请已提交");
            default:
                throw new BusinessException("不支持的操作类型");
        }
    }
    
    /**
     * 获取用户时间段占用情况
     */
    @GetMapping("/schedule")
    @Operation(summary = "获取用户时间段占用情况", description = "获取当前用户从今天开始未来 15 天的时间段占用情况")
    public Result<List<UserScheduleResponse>> getUserSchedule(Principal principal) {
        Long userId = getCurrentUserId(principal);
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(14); // 15 天
        
        List<UserScheduleResponse> schedule = customerOrderService.getUserSchedule(userId, startDate, endDate);
        return Result.success(schedule);
    }
}
