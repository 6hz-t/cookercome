package com.hs.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hs.backend.common.Result;
import com.hs.backend.entity.User;
import com.hs.backend.service.impl.SysUserServiceImpl;
import com.hs.backend.vo.UserQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")

public class SysUserController {

    @Autowired  // 自动注入Service
    private SysUserServiceImpl sysUserService;

    // 1. 获取用户列表（分页+搜索）
    @GetMapping("/list")
    public Result<Object> getUserList(UserQueryVo queryVo) {
        IPage<User> userList = sysUserService.getUserList(queryVo);
        return Result.success(userList);
    }

    // 2. 切换用户状态
    @PutMapping("/status/{userId}")
    public Result<Boolean> changeUserStatus(
            @PathVariable Long userId,  // 路径中的用户ID
            @RequestParam String status  // 请求参数：状态（active/disabled）
    ) {
        boolean success = sysUserService.changeUserStatus(userId, status);
        if (success) {
            return Result.success(true);
        } else {
            return Result.error("用户不存在或操作失败");
        }
    }

    // 3. 重置用户密码
    @PutMapping("/reset-password/{userId}")
    public Result<Boolean> resetPassword(@PathVariable Long userId) {
        boolean success = sysUserService.resetPassword(userId);
        if (success) {
            return Result.success(true);
        } else {
            return Result.error("用户不存在或操作失败");
        }
    }


}
