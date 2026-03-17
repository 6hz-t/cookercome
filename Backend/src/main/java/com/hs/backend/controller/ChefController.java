package com.hs.backend.controller;

import com.hs.backend.common.Result;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.service.ChefInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 厨师控制器
 */
@Tag(name = "厨师管理", description = "厨师列表查询、详情查看、审核等厨师相关接口")
@RestController
@RequestMapping("/api/chef")
public class ChefController {

    private final ChefInfoService chefInfoService;

    public ChefController(ChefInfoService chefInfoService) {
        this.chefInfoService = chefInfoService;
    }

    /**
     * 分页查询厨师列表
     */
//    @GetMapping("/list")
//    public Result<Page<ChefInfo>> getChefList(
//            @RequestParam(defaultValue = "1") Integer page,
//            @RequestParam(defaultValue = "10") Integer size,
//            @RequestParam(required = false) String specialty,
//            @RequestParam(required = false) Integer level
//    ) {
//        Page<ChefInfo> chefPage = chefInfoService.getChefPage(page, size, specialty, level);
//        return Result.success(chefPage);
//    }
    @GetMapping("/list")
    @Operation(summary = "获取厨师列表", description = "获取厨师列表信息（测试接口）")
    public Result<String> getChefList() {
        return Result.success("成功调用");
    }
    /**
     * 获取厨师详情
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "获取厨师详情", description = "根据厨师 ID 获取厨师详细信息")
    public Result<ChefInfo> getChefDetail(@PathVariable Long id) {
        ChefInfo chefInfo = chefInfoService.getChefDetail(id);
        return Result.success(chefInfo);
    }

    /**
     * 搜索附近厨师
     */
    @GetMapping("/nearby")
    @Operation(summary = "搜索附近厨师", description = "根据经纬度搜索指定半径范围内的厨师")
    public Result<List<ChefInfo>> getNearbyChefs(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "10") Integer radius
    ) {
        List<ChefInfo> chefInfos = chefInfoService.getNearbyChefs(longitude, latitude, radius);
        return Result.success(chefInfos);
    }

    /**
     * 创建厨师信息（仅管理员）
     */
    @PostMapping
    @Operation(summary = "创建厨师信息", description = "创建新的厨师信息（需要管理员权限）")
    public Result<ChefInfo> createChef(@RequestBody ChefInfo chefInfo) {
        ChefInfo createdChefInfo = chefInfoService.createChef(chefInfo);
        return Result.success(createdChefInfo);
    }

    /**
     * 更新厨师信息
     */
    @PutMapping
    @Operation(summary = "更新厨师信息", description = "更新指定厨师的信息")
    public Result<String> updateChef(@RequestBody ChefInfo chefInfo) {
        chefInfoService.updateChef(chefInfo);
        return Result.success("更新成功");
    }

    /**
     * 审核厨师（仅管理员）
     */
    @PostMapping("/audit/{id}")
    @Operation(summary = "审核厨师", description = "管理员审核厨师资质，可设置通过或拒绝")
    public Result<String> auditChef(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String reason
    ) {
        chefInfoService.auditChef(id, status, reason);
        return Result.success("审核成功");
    }
}
