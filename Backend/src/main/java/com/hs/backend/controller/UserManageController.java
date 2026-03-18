package com.hs.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hs.backend.common.Result;
import com.hs.backend.entity.AdminInfo;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.entity.CustomerInfo;
import com.hs.backend.entity.User;
import com.hs.backend.service.AdminInfoService;
import com.hs.backend.service.ChefInfoService;
import com.hs.backend.service.CustomerInfoService;
import com.hs.backend.service.UserManageService;
import com.hs.backend.vo.UserDetailVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user-manage")
@Tag(name = "用户管理")
public class UserManageController {

    @Autowired
    private UserManageService userManageService;

    @Autowired
    private AdminInfoService adminInfoService;

    @Autowired
    private ChefInfoService chefInfoService;

    @Autowired
    private CustomerInfoService customerInfoService;

    /**
     * 分页查询用户列表（支持按ID/姓名搜索）
     */
    @GetMapping("/list")
    public Result<IPage<UserDetailVO>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword
    ) {
        Page<User> page = new Page<>(pageNum, pageSize);
        IPage<User> userPage;

        // ========== 核心新增：ID/姓名搜索逻辑 ==========
        if (StringUtils.hasText(keyword)) {
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            boolean isIdSearch = false;

            // 1. 尝试按ID搜索（关键词为数字则匹配user.id）
            try {
                Long userId = Long.parseLong(keyword);
                userWrapper.eq(User::getId, userId);
                isIdSearch = true;
            } catch (NumberFormatException e) {
                // 非数字，后续按姓名搜索
            }

            // 执行ID搜索
            userPage = userManageService.page(page, userWrapper);

            // 2. 若ID搜索无结果，按姓名搜索各角色表
            if (!isIdSearch || userPage.getRecords().isEmpty()) {
                List<Long> matchUserIds = new ArrayList<>();

                // 搜索管理员姓名（AdminInfo.realName）
                List<AdminInfo> adminList = adminInfoService.list(
                        new LambdaQueryWrapper<AdminInfo>().like(AdminInfo::getRealName, keyword)
                );
                matchUserIds.addAll(adminList.stream().map(AdminInfo::getUserId).collect(Collectors.toList()));

                // 搜索厨师姓名（ChefInfo.realName）
                List<ChefInfo> chefList = chefInfoService.list(
                        new LambdaQueryWrapper<ChefInfo>().like(ChefInfo::getRealName, keyword)
                );
                matchUserIds.addAll(chefList.stream()
                        .map(chef -> Long.valueOf(chef.getUserId()))
                        .collect(Collectors.toList()));

                // 搜索客户姓名（CustomerInfo.realName）
                List<CustomerInfo> customerList = customerInfoService.list(
                        new LambdaQueryWrapper<CustomerInfo>().like(CustomerInfo::getRealName, keyword)
                );
                matchUserIds.addAll(customerList.stream().map(CustomerInfo::getUserId).collect(Collectors.toList()));

                // 根据姓名匹配的userId查询user表
                if (!matchUserIds.isEmpty()) {
                    userWrapper.clear();
                    userWrapper.in(User::getId, matchUserIds);
                    userPage = userManageService.page(page, userWrapper);
                } else {
                    // 无姓名匹配结果，返回空分页
                    userPage = new Page<>(pageNum, pageSize);
                }
            }
        } else {
            // 无关键词，查询全部用户
            userPage = userManageService.getUserList(page, keyword);
        }

        // VO转换逻辑
        IPage<UserDetailVO> detailPage = userPage.convert(user -> {
            UserDetailVO vo = new UserDetailVO();
            vo.setUserId(user.getId());
            vo.setPhone(user.getPhone());
            vo.setRole(user.getRole());
            vo.setCreateTime(user.getCreateTime());

            Integer role = user.getRole();
            Long userId = user.getId();

            // 0-客户
            if (role == 0) {
                CustomerInfo customerInfo = customerInfoService.getByUserId(userId);
                vo.setCustomerInfo(customerInfo);
                vo.setName(customerInfo != null ? customerInfo.getRealName() : "未知");
                vo.setStatus(String.valueOf(customerInfo != null ? customerInfo.getStatus() : 1));
            }
            // 1-厨师
            else if (role == 1) {
                ChefInfo chefInfo = chefInfoService.getByUserId(userId);
                vo.setChefInfo(chefInfo);
                vo.setName(chefInfo != null ? chefInfo.getRealName() : "未知");
                vo.setStatus(String.valueOf(chefInfo != null ? chefInfo.getStatus() : 1));
            }
            // 2-管理员
            else if (role == 2) {
                AdminInfo adminInfo = adminInfoService.getByUserId(userId);
                vo.setAdminInfo(adminInfo);
                vo.setName(adminInfo != null ? adminInfo.getRealName() : "未知");
                vo.setStatus(String.valueOf(adminInfo != null ? adminInfo.getStatus() : 1));
            }

            return vo;
        });

        return Result.success(detailPage);
    }

    /**
     * 单个用户详情（保持不变）
     */
    @GetMapping("/detail/{userId}")
    public Result<UserDetailVO> getUserDetail(@PathVariable Long userId) {
        User user = userManageService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        UserDetailVO vo = new UserDetailVO();
        vo.setUserId(user.getId());
        vo.setPhone(user.getPhone());
        vo.setRole(user.getRole());
        vo.setCreateTime(user.getCreateTime());

        Integer role = user.getRole();

        if (role == 0) {
            CustomerInfo customerInfo = customerInfoService.getByUserId(userId);
            vo.setCustomerInfo(customerInfo);
            vo.setName(customerInfo != null ? customerInfo.getRealName() : "未知");
            vo.setStatus(String.valueOf(customerInfo != null ? customerInfo.getStatus() : 1));
        } else if (role == 1) {
            ChefInfo chefInfo = chefInfoService.getByUserId(userId);
            vo.setChefInfo(chefInfo);
            vo.setName(chefInfo != null ? chefInfo.getRealName() : "未知");
            vo.setStatus(String.valueOf(chefInfo != null ? chefInfo.getStatus() : 1));
        } else if (role == 2) {
            AdminInfo adminInfo = adminInfoService.getByUserId(userId);
            vo.setAdminInfo(adminInfo);
            vo.setName(adminInfo != null ? adminInfo.getRealName() : "未知");
            vo.setStatus(String.valueOf(adminInfo != null ? adminInfo.getStatus() : 1));
        }

        return Result.success(vo);
    }

    /**
     * 修改用户状态（保持不变）
     */
    @PutMapping("/status/{userId}")
    public Result<Boolean> changeUserStatus(
            @PathVariable Long userId,
            @RequestParam Integer status
    ) {
        if (status != 0 && status != 1) {
            return Result.error("状态只能是 0(禁用) 或 1(启用)");
        }

        User user = userManageService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        Integer role = user.getRole();
        boolean success = false;

        // 0：客户 → 修改 CustomerInfo.status
        if (role == 0) {
            CustomerInfo customerInfo = customerInfoService.getByUserId(userId);
            if (customerInfo == null) {
                return Result.error("客户信息不存在");
            }
            customerInfo.setStatus(status);
            success = customerInfoService.updateById(customerInfo);
        }
        // 1：厨师 → 修改 ChefInfo.status
        else if (role == 1) {
            ChefInfo chefInfo = chefInfoService.getByUserId(userId);
            if (chefInfo == null) {
                return Result.error("厨师信息不存在");
            }
            chefInfo.setStatus(status);
            success = chefInfoService.updateById(chefInfo);
        }
        // 2：管理员 → 修改 AdminInfo.status
        else if (role == 2) {
            AdminInfo adminInfo = adminInfoService.getByUserId(userId);
            if (adminInfo == null) {
                return Result.error("管理员信息不存在");
            }
            adminInfo.setStatus(status);
            success = adminInfoService.updateById(adminInfo);
        }
        // 未知角色
        else {
            return Result.error("未知角色");
        }

        return success ? Result.success(true) : Result.error("状态修改失败");
    }

    /**
     * 重置密码（保持不变）
     */
    @PutMapping("/reset-password/{userId}")
    public Result<Boolean> resetPassword(@PathVariable Long userId) {
        boolean success = userManageService.resetPassword(userId);
        return success ? Result.success(true) : Result.error("重置密码失败");
    }
}