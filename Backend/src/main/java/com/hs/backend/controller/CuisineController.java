package com.hs.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hs.backend.common.Result;
import com.hs.backend.entity.Cuisine;
import com.hs.backend.service.CuisineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜系控制器
 */
@Tag(name = "菜系管理", description = "菜系列表查询、新增、编辑、删除等菜系相关接口")
@RestController
@RequestMapping("/api/cuisine")
public class CuisineController {

    @Autowired
    private CuisineService cuisineService;

    /**
     * 分页查询菜系列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取菜系列表", description = "分页查询菜系列表，支持关键词搜索")
    public Result<Page<Cuisine>> getCuisineList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword
    ) {
        Page<Cuisine> cuisinePage = cuisineService.getCuisinePage(page, size, keyword);
        return Result.success(cuisinePage);
    }

    /**
     * 获取菜系详情
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "获取菜系详情", description = "根据菜系 ID 获取菜系详细信息")
    public Result<Cuisine> getCuisineDetail(@PathVariable Long id) {
        Cuisine cuisine = cuisineService.getById(id);
        if (cuisine == null) {
            return Result.error("菜系不存在");
        }
        return Result.success(cuisine);
    }

    /**
     * 新增菜系
     */
    @PostMapping
    @Operation(summary = "新增菜系", description = "创建新的菜系信息")
    public Result<Cuisine> addCuisine(@RequestBody Cuisine cuisine) {
        // 检查名称是否重复
        Cuisine existing = cuisineService.getByName(cuisine.getCuisineName());
        if (existing != null) {
            return Result.error("菜系名称已存在");
        }

        // 默认状态为启用
        if (cuisine.getStatus() == null) {
            cuisine.setStatus(1);
        }

        cuisineService.save(cuisine);
        return Result.success(cuisine);
    }

    /**
     * 更新菜系
     */
    @PutMapping
    @Operation(summary = "更新菜系", description = "更新指定菜系的信息")
    public Result<String> updateCuisine(@RequestBody Cuisine cuisine) {
        Cuisine existing = cuisineService.getById(cuisine.getId());
        if (existing == null) {
            return Result.error("菜系不存在");
        }

        // 如果修改了名称，检查是否与其他菜系重复
        if (!existing.getCuisineName().equals(cuisine.getCuisineName())) {
            Cuisine duplicate = cuisineService.getByName(cuisine.getCuisineName());
            if (duplicate != null && !duplicate.getId().equals(cuisine.getId())) {
                return Result.error("菜系名称已存在");
            }
        }

        cuisineService.updateById(cuisine);
        return Result.success("更新成功");
    }

    /**
     * 删除菜系
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜系", description = "根据 ID 删除菜系")
    public Result<String> deleteCuisine(@PathVariable Long id) {
        Cuisine cuisine = cuisineService.getById(id);
        if (cuisine == null) {
            return Result.error("菜系不存在");
        }

        cuisineService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 批量删除菜系
     */
    @DeleteMapping
    @Operation(summary = "批量删除菜系", description = "批量删除指定 ID 的菜系")
    public Result<String> deleteCuisines(@RequestBody List<Long> ids) {
        cuisineService.removeByIds(ids);
        return Result.success("批量删除成功");
    }
}
