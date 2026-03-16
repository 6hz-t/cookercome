package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.backend.entity.User;
import com.hs.backend.mapper.UserManageMapper;
import com.hs.backend.service.UserManageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户管理Service实现类
 */
@Service
public class UserManageServiceImpl extends ServiceImpl<UserManageMapper, User> implements UserManageService {

    /**
     * 分页查询用户列表（支持ID关键词搜索，姓名搜索在Controller层处理）
     * 注：姓名搜索需关联角色表，因此在Controller层实现更灵活
     */
    @Override
    public IPage<User> getUserList(Page<User> page, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 仅查询User表存在的字段，避免Select * 报错
        wrapper.select(User::getId, User::getPhone, User::getRole, User::getCreateTime);

        // 关键词为数字时，按ID搜索（姓名搜索在Controller层处理）
        if (StringUtils.hasText(keyword)) {
            try {
                Long userId = Long.parseLong(keyword);
                wrapper.eq(User::getId, userId);
            } catch (NumberFormatException e) {
                // 非数字（姓名）→ 此处不处理，交给Controller层关联角色表搜索
            }
        }

        // 按创建时间倒序
        wrapper.orderByDesc(User::getCreateTime);

        // 执行分页查询
        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * 按ID查询用户（仅查存在的字段）
     */
    @Override
    public User getById(Long userId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(User::getId, User::getPhone, User::getRole, User::getCreateTime)
                .eq(User::getId, userId);
        return baseMapper.selectOne(wrapper);
    }

    /**
     * 重置密码：仅修改User表的password字段（该字段存在，合法操作）
     */
    @Override
    public boolean resetPassword(Long userId) {
        User user = baseMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        // 仅修改password字段，其他字段不碰
        user.setPassword("123456");
        return baseMapper.updateById(user) > 0;
    }
}