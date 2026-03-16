package com.hs.backend.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hs.backend.entity.User;
import com.hs.backend.vo.UserQueryVo;

public interface SysUserService {

    // 获取用户列表（分页+搜索）
    IPage<User> getUserList(UserQueryVo queryVo);

    // 切换用户状态
    boolean changeUserStatus(Long userId, String status);

    // 重置用户密码
    boolean resetPassword(Long userId);
}
