package com.hs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hs.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserManageMapper extends BaseMapper<User> {

}
