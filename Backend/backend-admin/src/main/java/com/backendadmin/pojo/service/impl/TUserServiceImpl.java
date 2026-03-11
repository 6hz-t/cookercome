package com.admin.backendadmin.pojo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.admin.backendadmin.pojo.entity.TUser;
import com.admin.backendadmin.pojo.service.TUserService;
import com.admin.backendadmin.pojo.mapper.TUserMapper;
import org.springframework.stereotype.Service;

/**
* @author air
* @description 针对表【t_user(统一用户表（客户 + 厨师 + 管理员）)】的数据库操作Service实现
* @createDate 2026-03-10 16:20:13
*/
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser>
    implements TUserService{

}




