package com.hs.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hs.backend.common.Result;
import com.hs.backend.entity.ChefDish;
import com.hs.backend.service.ChefDishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 厨师菜品控制器
 */
@Tag(name = "厨师菜品管理", description = "菜品列表查询、新增、编辑、删除等菜品相关接口")
@RestController
@RequestMapping("/api/chef-dish")
public class ChefDishController {

    @Autowired
    private ChefDishService chefDishService;

    /**
     * 分页查询菜品列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取菜品列表", description = "分页查询菜品列表，支持关键词搜索和按菜系筛选")
    public Result<Page<ChefDish>> getDishList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer cuisineId
    ) {
        Page<ChefDish> dishPage = chefDishService.getDishPage(page, size, keyword, cuisineId);
        return Result.success(dishPage);
    }

    /**
     * 获取菜品详情
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "获取菜品详情", description = "根据菜品 ID 获取菜品详细信息")
    public Result<ChefDish> getDishDetail(@PathVariable Long id) {
        ChefDish chefDish = chefDishService.getById(id);
        if (chefDish == null) {
            return Result.error("菜品不存在");
        }
        return Result.success(chefDish);
    }

    /**
     * 新增菜品
     */
    @PostMapping
    @Operation(summary = "新增菜品", description = "创建新的菜品信息")
    public Result<ChefDish> addDish(@RequestBody ChefDish chefDish) {
        if (chefDish.getPrice() == null) {
            chefDish.setPrice(BigDecimal.ZERO);
        }
        if (chefDish.getIsFeatured() == null) {
            chefDish.setIsFeatured(0);
        }
        if (chefDish.getDishType() == null) {
            chefDish.setDishType("");
        }
        if (chefDish.getDescription() == null) {
            chefDish.setDescription("");
        }
        // 如果没有设置 userId，默认使用测试厨师 ID（实际应该从 Token 中获取）
        if (chefDish.getUserId() == null) {
            chefDish.setUserId(2L);
        }
        chefDishService.save(chefDish);
        return Result.success(chefDish);
    }

    /**
     * 更新菜品
     */
    @PutMapping
    @Operation(summary = "更新菜品", description = "更新指定菜品的信息")
    public Result<String> updateDish(@RequestBody ChefDish chefDish) {
        ChefDish existing = chefDishService.getById(chefDish.getId());
        if (existing == null) {
            return Result.error("菜品不存在");
        }

        chefDishService.updateById(chefDish);
        return Result.success("更新成功");
    }

    /**
     * 删除菜品
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜品", description = "根据 ID 删除菜品")
    public Result<String> deleteDish(@PathVariable Long id) {
        ChefDish chefDish = chefDishService.getById(id);
        if (chefDish == null) {
            return Result.error("菜品不存在");
        }

        chefDishService.removeById(id);
        return Result.success("删除成功");
    }
}
