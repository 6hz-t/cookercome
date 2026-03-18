package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.mapper.ChefInfoMapper;
import com.hs.backend.service.ChefInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 厨师服务实现类
 */
@Slf4j
@Service
public class ChefInfoInfoServiceImpl extends ServiceImpl<ChefInfoMapper, ChefInfo> implements ChefInfoService {

    @Override
    public Page<ChefInfo> getChefPage(Integer page, Integer size, String specialty, Integer level) {
        Page<ChefInfo> chefPage = new Page<>(page, size);

        LambdaQueryWrapper<ChefInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChefInfo::getAuditStatus, 1)  // 只查询已审核通过的
                .eq(ChefInfo::getStatus, 1); // 只查询启用的

        if (StringUtils.hasText(specialty)) {
            // specialty 字段不存在，暂时忽略此条件
            log.warn("specialty 字段不存在，忽略此搜索条件");
        }

        if (level != null) {
            // 使用 chefLevel 代替 level
            wrapper.eq(ChefInfo::getChefLevel, level);
        }

        // 按完成订单数和评分排序（rating 字段不存在，只用完成订单数）
        wrapper.orderByDesc(ChefInfo::getCompletedOrders);

        return page(chefPage, wrapper);
    }

    @Override
    public ChefInfo getChefDetail(Long id) {
        ChefInfo chefInfo = getById(id);
        if (chefInfo == null) {
            throw new BusinessException("厨师不存在");
        }
        return chefInfo;
    }

    @Override
    public List<ChefInfo> getNearbyChefs(Double longitude, Double latitude, Integer radius) {
        // 这里简化实现，实际应该使用数据库的空间查询或百度地图 API
        LambdaQueryWrapper<ChefInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChefInfo::getAuditStatus, 1)
                .eq(ChefInfo::getStatus, 1)
                .isNotNull(ChefInfo::getLongitude)
                .isNotNull(ChefInfo::getLatitude);

        List<ChefInfo> chefInfos = list(wrapper);

        // 计算距离并过滤（简化版，实际应该使用 Haversine 公式）
        return chefInfos.stream()
                .filter(chefInfo -> {
                    if (chefInfo.getLongitude() == null || chefInfo.getLatitude() == null) {
                        return false;
                    }
                    double distance = calculateDistance(longitude, latitude,
                            chefInfo.getLongitude().doubleValue(), chefInfo.getLatitude().doubleValue());
                    return distance <= radius;
                })
                .sorted((c1, c2) -> {
                    double d1 = calculateDistance(longitude, latitude,
                            c1.getLongitude().doubleValue(), c1.getLatitude().doubleValue());
                    double d2 = calculateDistance(longitude, latitude,
                            c2.getLongitude().doubleValue(), c2.getLatitude().doubleValue());
                    return Double.compare(d1, d2);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ChefInfo createChef(ChefInfo chefInfo) {
        save(chefInfo);
        return chefInfo;
    }

    @Override
    public void updateChef(ChefInfo chefInfo) {
        updateById(chefInfo);
    }

    @Override
    public void auditChef(Long chefId, Integer status, String reason) {
        ChefInfo chefInfo = getById(chefId);
        if (chefInfo == null) {
            throw new BusinessException("厨师不存在");
        }

        chefInfo.setAuditStatus(status);
        // onlineStatus 字段不存在，移除相关代码
        // if (status == 1) {
        //     chefInfo.setOnlineStatus(1); // 审核通过自动上架
        // } else if (status == 2) {
        //     chefInfo.setOnlineStatus(0); // 审核拒绝自动下架
        // }

        updateById(chefInfo);
    }

    /**
     * 根据用户 ID 获取厨师信息
     */
    @Override
    public ChefInfo getByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<ChefInfo>()
                .eq(ChefInfo::getUserId, userId));
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
