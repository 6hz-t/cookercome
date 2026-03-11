package com.hs.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hs.backend.common.Result;
import com.hs.backend.entity.Chef;
import com.hs.backend.service.ChefService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 厨师控制器
 */
@RestController
@RequestMapping("/api/chef")
public class ChefController {

    private final ChefService chefService;

    public ChefController(ChefService chefService) {
        this.chefService = chefService;
    }

    /**
     * 分页查询厨师列表
     */
//    @GetMapping("/list")
//    public Result<Page<Chef>> getChefList(
//            @RequestParam(defaultValue = "1") Integer page,
//            @RequestParam(defaultValue = "10") Integer size,
//            @RequestParam(required = false) String specialty,
//            @RequestParam(required = false) Integer level
//    ) {
//        Page<Chef> chefPage = chefService.getChefPage(page, size, specialty, level);
//        return Result.success(chefPage);
//    }
    @GetMapping("/list")
    public Result<String> getChefList() {
        return Result.success("成功调用");
    }
    /**
     * 获取厨师详情
     */
    @GetMapping("/detail/{id}")
    public Result<Chef> getChefDetail(@PathVariable Long id) {
        Chef chef = chefService.getChefDetail(id);
        return Result.success(chef);
    }

    /**
     * 搜索附近厨师
     */
    @GetMapping("/nearby")
    public Result<List<Chef>> getNearbyChefs(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "10") Integer radius
    ) {
        List<Chef> chefs = chefService.getNearbyChefs(longitude, latitude, radius);
        return Result.success(chefs);
    }

    /**
     * 创建厨师信息（仅管理员）
     */
    @PostMapping
    public Result<Chef> createChef(@RequestBody Chef chef) {
        Chef createdChef = chefService.createChef(chef);
        return Result.success(createdChef);
    }

    /**
     * 更新厨师信息
     */
    @PutMapping
    public Result<String> updateChef(@RequestBody Chef chef) {
        chefService.updateChef(chef);
        return Result.success("更新成功");
    }

    /**
     * 审核厨师（仅管理员）
     */
    @PostMapping("/audit/{id}")
    public Result<String> auditChef(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String reason
    ) {
        chefService.auditChef(id, status, reason);
        return Result.success("审核成功");
    }
}
