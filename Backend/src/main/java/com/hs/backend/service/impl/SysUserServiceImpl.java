package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.backend.entity.User;
import com.hs.backend.mapper.SysUserMapper;
import com.hs.backend.service.SysUserService;
import com.hs.backend.vo.UserQueryVo;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, User> implements SysUserService {

    // 分页查询用户列表（支持搜索）
    @Override
    public IPage<User> getUserList(UserQueryVo queryVo) {
        // 1. 创建分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(queryVo.getPageNum(), queryVo.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 如果有搜索关键词，模糊查询用户名 或 精确匹配ID
        if (queryVo.getKeyword() != null && !queryVo.getKeyword().isEmpty()) {
            wrapper.like(User::getName, queryVo.getKeyword())
                    .or()
                    .eq(User::getId, Long.parseLong(queryVo.getKeyword()));
        }

        // 3. 执行分页查询
        return baseMapper.selectPage(page, wrapper);
    }

    // 切换用户状态
    @Override
    public boolean changeUserStatus(Long userId, String status) {
        // 1. 查询用户是否存在
        User user = baseMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        // 2. 修改状态
        user.setStatus(status);
        // 3. 保存到数据库
        return baseMapper.updateById(user) > 0;
    }

    // 重置用户密码（默认123456）
    @Override
    public boolean resetPassword(Long userId) {
        // 1. 查询用户是否存在
        User user = baseMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        // 2. 重置密码
        user.setPassword("123456");
        // 3. 保存到数据库
        return baseMapper.updateById(user) > 0;
    }
}
