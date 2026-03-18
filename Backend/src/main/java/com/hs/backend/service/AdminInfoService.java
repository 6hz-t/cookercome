package com.hs.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hs.backend.entity.AdminInfo;

import java.util.List;


public interface AdminInfoService {

    /*
    * 根据userId查询管理员信息
    * */
    AdminInfo getByUserId(Long userId);

    /*
    * 根据userId修改管理员信息
    * */
    boolean updateById(AdminInfo adminInfo);

    /*
    * 查询管理员列表
    * */
    List<AdminInfo> list(LambdaQueryWrapper<AdminInfo> like);
}
