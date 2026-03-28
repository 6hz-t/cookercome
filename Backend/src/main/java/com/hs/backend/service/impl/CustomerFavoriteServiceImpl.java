package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.dto.response.FavoriteChefResponse;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.entity.CustomerFavorite;
import com.hs.backend.entity.User;
import com.hs.backend.mapper.ChefInfoMapper;
import com.hs.backend.mapper.CustomerFavoriteMapper;
import com.hs.backend.mapper.UserMapper;
import com.hs.backend.service.CustomerFavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerFavoriteServiceImpl implements CustomerFavoriteService {

    private final CustomerFavoriteMapper customerFavoriteMapper;
    private final ChefInfoMapper chefInfoMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFavorite(Long customerId, String chefId) {
        if (customerFavoriteMapper.exists(customerId, chefId)) {
            throw new BusinessException("该厨师已在收藏夹中");
        }

        CustomerFavorite favorite = new CustomerFavorite();
        favorite.setCustomerId(customerId);
        favorite.setChefId(chefId);
        favorite.setCreateTime(LocalDateTime.now());

        int rows = customerFavoriteMapper.insert(favorite);
        if (rows <= 0) {
            throw new BusinessException("添加收藏失败");
        }

        log.info("用户 {} 收藏了厨师 {}", customerId, chefId);
    }

    @Override
    public boolean isFavorited(Long customerId, String chefId) {
        return customerFavoriteMapper.exists(customerId, chefId);
    }

    @Override
    public List<FavoriteChefResponse> getFavoriteList(Long customerId) {
        QueryWrapper<CustomerFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_id", customerId)
                .orderByDesc("create_time");
        List<CustomerFavorite> favorites = customerFavoriteMapper.selectList(queryWrapper);

        if (favorites.isEmpty()) {
            return List.of();
        }

        List<String> chefUserIds = favorites.stream()
                .map(CustomerFavorite::getChefId)
                .collect(Collectors.toList());

        QueryWrapper<ChefInfo> chefQueryWrapper = new QueryWrapper<>();
        chefQueryWrapper.in("user_id", chefUserIds)
                .eq("audit_status", 1)
                .eq("status", 1);
        List<ChefInfo> chefs = chefInfoMapper.selectList(chefQueryWrapper);

        return chefs.stream()
                .map(chef -> {
                    FavoriteChefResponse response = new FavoriteChefResponse();
                    response.setChefId(chef.getUserId());
                    response.setRealName(chef.getRealName());
                    response.setGender(chef.getGender());
                    response.setChefLevel(chef.getChefLevel());
                    response.setExperienceYears(chef.getExperienceYears());
                    response.setCompletedOrders(chef.getCompletedOrders());
                    response.setMinPrice(chef.getMinPrice());
                    response.setIntroduction(chef.getIntroduction());
                    response.setAvatarUrl(chef.getAvatarUrl());

                    User user = userMapper.selectById(Long.parseLong(chef.getUserId()));
                    if (user != null) {
                        response.setPhone(user.getPhone());
                    }

                    CustomerFavorite favorite = favorites.stream()
                            .filter(f -> f.getChefId().equals(chef.getUserId()))
                            .findFirst()
                            .orElse(null);
                    if (favorite != null) {
                        response.setFavoriteTime(favorite.getCreateTime());
                    }

                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(Long customerId, String chefId) {
        QueryWrapper<CustomerFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_id", customerId)
                .eq("chef_id", chefId);
        CustomerFavorite favorite = customerFavoriteMapper.selectOne(queryWrapper);

        if (favorite == null) {
            throw new BusinessException("未找到该收藏记录");
        }

        int rows = customerFavoriteMapper.delete(queryWrapper);
        if (rows <= 0) {
            throw new BusinessException("取消收藏失败");
        }

        log.info("用户 {} 取消收藏厨师 {}", customerId, chefId);
    }
}
