package com.hs.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hs.backend.entity.CustomerInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户信息 Mapper 接口
 */
@Mapper
public interface CustomerInfoMapper extends BaseMapper<CustomerInfo> {

}
