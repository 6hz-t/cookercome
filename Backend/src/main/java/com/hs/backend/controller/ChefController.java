package com.hs.backend.controller;

import com.hs.backend.common.Result;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.dto.ChefProfileDTO;
import com.hs.backend.dto.OrderDTO;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.service.ChefInfoService;
import com.hs.backend.service.ChefOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Chef controller.
 */
@Tag(name = "厨师管理", description = "厨师端相关接口")
@RestController
@RequestMapping("/api/chef")
public class ChefController {

    private final ChefInfoService chefInfoService;
    private final ChefOrderService chefOrderService;

    public ChefController(ChefInfoService chefInfoService, ChefOrderService chefOrderService) {
        this.chefInfoService = chefInfoService;
        this.chefOrderService = chefOrderService;
    }

    @GetMapping("/list")
    @Operation(summary = "厨师列表")
    public Result<String> getChefList() {
        return Result.success("ok");
    }

    @GetMapping("/profile")
    @Operation(summary = "获取厨师个人信息", description = "根据用户 ID 获取厨师的详细信息")
    public Result<ChefProfileDTO> getChefProfile(@RequestParam Long userId) {
        ChefInfo chefInfo = chefInfoService.getByUserId(userId);
        if (chefInfo == null) {
            ChefProfileDTO empty = new ChefProfileDTO();
            empty.setId(0L);
            empty.setUserId(String.valueOf(userId));
            empty.setGender(0);
            empty.setExperienceYears(0);
            empty.setChefLevel(0);
            empty.setMinPrice(java.math.BigDecimal.ZERO);
            empty.setCompletedOrders(0);
            empty.setLatitude(java.math.BigDecimal.ZERO);
            empty.setLongitude(java.math.BigDecimal.ZERO);
            empty.setStatus(0);
            return Result.success(empty);
        }
        return Result.success(toProfileDTO(chefInfo));
    }

    @PostMapping("/profile")
    @Operation(summary = "创建或更新厨师信息", description = "创建新的厨师档案或更新现有厨师的信息")
    public Result<ChefProfileDTO> saveChefProfile(@RequestBody ChefProfileDTO profile) {
        if (profile == null || profile.getUserId() == null || profile.getUserId().isBlank()) {
            throw new BusinessException("userId is required");
        }
        Long userId;
        try {
            userId = Long.valueOf(profile.getUserId());
        } catch (NumberFormatException ex) {
            throw new BusinessException("userId is invalid");
        }

        ChefInfo chefInfo = chefInfoService.getByUserId(userId);
        if (chefInfo == null) {
            chefInfo = new ChefInfo();
            chefInfo.setUserId(profile.getUserId());
            chefInfo.setAuditStatus(0);
        }

        chefInfo.setRealName(profile.getRealName());
        chefInfo.setGender(profile.getGender());
        chefInfo.setIdCardNo(profile.getIdCardNo());
        chefInfo.setPhone(profile.getPhone());
        chefInfo.setDetailAddress(profile.getDetailAddress());
        chefInfo.setExperienceYears(profile.getExperienceYears());
        chefInfo.setChefLevel(profile.getChefLevel());
        chefInfo.setMinPrice(profile.getMinPrice());
        chefInfo.setCompletedOrders(profile.getCompletedOrders());
        chefInfo.setIntroduction(profile.getIntroduction());
        chefInfo.setLatitude(profile.getLatitude());
        chefInfo.setLongitude(profile.getLongitude());
        chefInfo.setStatus(profile.getStatus());

        if (chefInfo.getId() == null) {
            chefInfoService.save(chefInfo);
        } else {
            chefInfoService.updateById(chefInfo);
        }

        return Result.success(toProfileDTO(chefInfo));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "厨师详情", description = "根据厨师 ID 获取详细信息")
    public Result<ChefInfo> getChefDetail(@PathVariable Long id) {
        ChefInfo chefInfo = chefInfoService.getChefDetail(id);
        return Result.success(chefInfo);
    }

    @GetMapping("/nearby")
    @Operation(summary = "附近厨师", description = "根据经纬度查询指定半径内的厨师")
    public Result<List<ChefInfo>> getNearbyChefs(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "10") Integer radius
    ) {
        List<ChefInfo> chefInfos = chefInfoService.getNearbyChefs(longitude, latitude, radius);
        return Result.success(chefInfos);
    }

    @PostMapping
    @Operation(summary = "创建厨师", description = "添加新的厨师信息")
    public Result<ChefInfo> createChef(@RequestBody ChefInfo chefInfo) {
        ChefInfo createdChefInfo = chefInfoService.createChef(chefInfo);
        return Result.success(createdChefInfo);
    }

    @PutMapping
    @Operation(summary = "更新厨师", description = "修改厨师信息")
    public Result<String> updateChef(@RequestBody ChefInfo chefInfo) {
        chefInfoService.updateChef(chefInfo);
        return Result.success("updated");
    }

    @PostMapping("/audit/{id}")
    @Operation(summary = "审核厨师", description = "管理员审核厨师资质，可设置审核状态和拒绝原因")
    public Result<String> auditChef(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String reason
    ) {
        chefInfoService.auditChef(id, status, reason);
        return Result.success("audited");
    }

    @PostMapping("/getHistoryOrders")
    @Operation(summary = "历史订单", description = "查询厨师的历史订单记录")
    public Result<List<OrderDTO>> getHistoryOrders(@RequestParam Long chefId) {
        List<OrderDTO> orders = chefOrderService.getHistoryOrders(chefId);
        return Result.success(orders);
    }

    @PostMapping("/getNewOrders")
    @Operation(summary = "待接单", description = "查询厨师的新订单（待接单）")
    public Result<List<OrderDTO>> getNewOrders(@RequestParam Long chefId) {
        List<OrderDTO> orders = chefOrderService.getPendingOrders(chefId);
        return Result.success(orders);
    }

    @PostMapping("/getOrders")
    @Operation(summary = "服务中订单", description = "查询厨师正在服务的订单")
    public Result<List<OrderDTO>> getOrders(@RequestParam Long chefId) {
        List<OrderDTO> orders = chefOrderService.getServingOrders(chefId);
        return Result.success(orders);
    }

    @PostMapping("/getTodayOrders")
    @Operation(summary = "今日概览", description = "查询厨师今天的订单统计概览")
    public Result<Map<String, Object>> getTodayOrders(@RequestParam Long chefId) {
        Map<String, Object> summary = chefOrderService.getTodayOrderSummary(chefId);
        return Result.success(summary);
    }

    @PostMapping("/acceptOrder")
    @Operation(summary = "接单", description = "厨师接受客户订单")
    public Result<String> acceptOrder(@RequestParam Long orderId, @RequestParam Long chefId) {
        chefOrderService.acceptOrder(orderId, chefId);
        return Result.success("accepted");
    }

    @PostMapping("/updateChefStatus")
    @Operation(summary = "更新厨师状态", description = "更新厨师的服务状态（营业/休息）")
    public Result<String> updateChefStatus(@RequestParam Long chefId, @RequestParam Integer status) {
        chefOrderService.updateChefStatus(chefId, status);
        return Result.success("updated");
    }

    @PostMapping("/updateOrderStatus")
    @Operation(summary = "更新订单状态", description = "更新订单的服务进度状态")
    public Result<String> updateOrderStatus(@RequestParam Long orderId, @RequestParam Integer status) {
        chefOrderService.updateOrderStatus(orderId, status);
        return Result.success("updated");
    }

    private ChefProfileDTO toProfileDTO(ChefInfo chefInfo) {
        ChefProfileDTO dto = new ChefProfileDTO();
        dto.setId(chefInfo.getId());
        dto.setUserId(chefInfo.getUserId());
        dto.setRealName(chefInfo.getRealName());
        dto.setGender(chefInfo.getGender());
        dto.setIdCardNo(chefInfo.getIdCardNo());
        dto.setPhone(chefInfo.getPhone());
        dto.setDetailAddress(chefInfo.getDetailAddress());
        dto.setExperienceYears(chefInfo.getExperienceYears());
        dto.setChefLevel(chefInfo.getChefLevel());
        dto.setMinPrice(chefInfo.getMinPrice());
        dto.setCompletedOrders(chefInfo.getCompletedOrders());
        dto.setIntroduction(chefInfo.getIntroduction());
        dto.setLatitude(chefInfo.getLatitude());
        dto.setLongitude(chefInfo.getLongitude());
        dto.setStatus(chefInfo.getStatus());
        return dto;
    }
}
