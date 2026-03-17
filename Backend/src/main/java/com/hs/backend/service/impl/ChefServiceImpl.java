package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.entity.Chef;
import com.hs.backend.mapper.ChefMapper;
import com.hs.backend.service.ChefService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 厨师服务实现类
 */
@Service
public class ChefServiceImpl extends ServiceImpl<ChefMapper, Chef> implements ChefService {

    @Override
    public Page<Chef> getChefPage(Integer page, Integer size, String specialty, Integer level) {
        Page<Chef> chefPage = new Page<>(page, size);

        LambdaQueryWrapper<Chef> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Chef::getAuditStatus, 1)  // 只查询已审核通过的
                .eq(Chef::getOnlineStatus, 1); // 只查询已上架的

        if (StringUtils.hasText(specialty)) {
            wrapper.like(Chef::getSpecialty, specialty);
        }

        if (level != null) {
            wrapper.eq(Chef::getLevel, level);
        }

        wrapper.orderByDesc(Chef::getRating)
                .orderByDesc(Chef::getServiceCount);

        return page(chefPage, wrapper);
    }

    @Override
    public Chef getChefDetail(Long id) {
        Chef chef = getById(id);
        if (chef == null) {
            throw new BusinessException("厨师不存在");
        }
        return chef;
    }

    @Override
    public List<Chef> getNearbyChefs(Double longitude, Double latitude, Integer radius) {
        // 这里简化实现，实际应该使用数据库的空间查询或百度地图 API
        LambdaQueryWrapper<Chef> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Chef::getAuditStatus, 1)
                .eq(Chef::getOnlineStatus, 1)
                .isNotNull(Chef::getLongitude)
                .isNotNull(Chef::getLatitude);

        List<Chef> chefs = list(wrapper);

        // 计算距离并过滤（简化版，实际应该使用 Haversine 公式）
        return chefs.stream()
                .filter(chef -> {
                    if (chef.getLongitude() == null || chef.getLatitude() == null) {
                        return false;
                    }
                    double distance = calculateDistance(longitude, latitude,
                            chef.getLongitude(), chef.getLatitude());
                    return distance <= radius;
                })
                .sorted((c1, c2) -> {
                    double d1 = calculateDistance(longitude, latitude,
                            c1.getLongitude(), c1.getLatitude());
                    double d2 = calculateDistance(longitude, latitude,
                            c2.getLongitude(), c2.getLatitude());
                    return Double.compare(d1, d2);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Chef createChef(Chef chef) {
        save(chef);
        return chef;
    }

    @Override
    public void updateChef(Chef chef) {
        updateById(chef);
    }

    @Override
    public void auditChef(Long chefId, Integer status, String reason) {
        Chef chef = getById(chefId);
        if (chef == null) {
            throw new BusinessException("厨师不存在");
        }

        chef.setAuditStatus(status);
        if (status == 1) {
            chef.setOnlineStatus(1); // 审核通过自动上架
        } else if (status == 2) {
            chef.setOnlineStatus(0); // 审核拒绝自动下架
        }

        updateById(chef);
    }

    /**
     * 计算两点之间的距离（简化版）
     */
    private double calculateDistance(Double lon1, Double lat1, Double lon2, Double lat2) {
        // 实际应该使用 Haversine 公式或调用百度地图 API
        double dLon = lon2 - lon1;
        double dLat = lat2 - lat1;
        return Math.sqrt(dLon * dLon + dLat * dLat) * 111; // 粗略估算
    }
}
