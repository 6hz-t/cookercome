package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.backend.entity.AdminInfo;
import com.hs.backend.mapper.AdminInfoMapper;
import com.hs.backend.service.AdminInfoService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

@Service
public class AdminInfoServiceImpl extends ServiceImpl<AdminInfoMapper, AdminInfo> implements AdminInfoService {

    /*
    * 根据userId查询管理员详情
    * */
    @Override
    public AdminInfo getByUserId(Long userId) {
        QueryWrapper<AdminInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return baseMapper.selectOne(queryWrapper);
    }

    /*
    * 根据id更新管理员详情
    * */
    @Override
    public boolean updateById(AdminInfo adminInfo) {
        return super.updateById(adminInfo);
    }

    /*
    * 根据条件查询管理员详情
    * */
    @Override
    public List<AdminInfo> list(LambdaQueryWrapper<AdminInfo> like) {
        return baseMapper.selectList(like);
    }
}
