package com.hs.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hs.backend.entity.User;

/**
 * 用户管理Service接口
 * 核心：仅操作User表存在的字段，状态/姓名操作路由到角色专属Service
 */
public interface UserManageService extends IService<User> {

    /**
     * 分页查询用户列表（支持关键词搜索）
     * @param page 分页参数
     * @param keyword 关键词（ID/姓名，为空则查全部）
     * @return 分页用户列表
     */
    IPage<User> getUserList(Page<User> page, String keyword);

    /**
     * 按ID查询用户
     * @param userId 用户ID
     * @return 用户基础信息
     */
    User getById(Long userId);

    /**
     * 重置用户密码（仅修改User表的password字段）
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean resetPassword(Long userId);
}