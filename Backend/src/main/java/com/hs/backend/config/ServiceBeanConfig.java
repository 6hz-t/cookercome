package com.hs.backend.config;

import com.hs.backend.mapper.ChefInfoMapper;
import com.hs.backend.mapper.CustomerFavoriteMapper;
import com.hs.backend.mapper.CustomerInfoMapper;
import com.hs.backend.mapper.OrderMapper;
import com.hs.backend.service.CustomerOrderQueryService;
import com.hs.backend.service.impl.CustomerOrderQueryServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 显式注册核心服务 Bean，避免在开发阶段因扫描差异导致注入失败。
 */
@Configuration
public class ServiceBeanConfig {

    @Bean
    public CustomerOrderQueryService customerOrderQueryService(OrderMapper orderMapper,
                                                               ChefInfoMapper chefInfoMapper,
                                                               CustomerInfoMapper customerInfoMapper,
                                                               CustomerFavoriteMapper customerFavoriteMapper) {
        return new CustomerOrderQueryServiceImpl(orderMapper, chefInfoMapper, customerInfoMapper, customerFavoriteMapper);
    }
}

