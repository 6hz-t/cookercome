package com.admin.backendadmin.pojo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.admin.backendadmin.pojo.entity.ChefCuisine;
import com.admin.backendadmin.pojo.service.ChefCuisineService;
import com.admin.backendadmin.pojo.mapper.ChefCuisineMapper;
import org.springframework.stereotype.Service;

/**
* @author air
* @description 针对表【t_chef_cuisine(厨师 - 菜系关联表（多对多）)】的数据库操作Service实现
* @createDate 2026-03-10 16:19:33
*/
@Service
public class ChefCuisineServiceImpl extends ServiceImpl<ChefCuisineMapper, ChefCuisine>
    implements ChefCuisineService{

}




