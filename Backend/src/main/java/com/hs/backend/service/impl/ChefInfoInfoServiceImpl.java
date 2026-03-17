package com.hs.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.backend.common.exception.BusinessException;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.entity.User;
import com.hs.backend.mapper.ChefInfoMapper;
import com.hs.backend.mapper.UserMapper;
import com.hs.backend.service.ChefInfoService;
import com.hs.backend.vo.ChefAuditVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 厨师服务实现类
 */
@Service
public class ChefInfoInfoServiceImpl extends ServiceImpl<ChefInfoMapper, ChefInfo> implements ChefInfoService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Page<ChefInfo> getChefPage(Integer page, Integer size, String specialty, Integer level) {
        Page<ChefInfo> chefPage = new Page<>(page, size);

        LambdaQueryWrapper<ChefInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChefInfo::getAuditStatus, 1)  // 只查询已审核通过的
                .eq(com.hs.backend.entity.ChefInfo::getOnlineStatus, 1); // 只查询已上架的

        if (StringUtils.hasText(specialty)) {
            wrapper.like(com.hs.backend.entity.ChefInfo::getSpecialty, specialty);
        }

        if (level != null) {
            wrapper.eq(com.hs.backend.entity.ChefInfo::getLevel, level);
        }

        wrapper.orderByDesc(com.hs.backend.entity.ChefInfo::getRating)
                .orderByDesc(com.hs.backend.entity.ChefInfo::getServiceCount);

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
        wrapper.eq(com.hs.backend.entity.ChefInfo::getAuditStatus, 1)
                .eq(com.hs.backend.entity.ChefInfo::getOnlineStatus, 1)
                .isNotNull(com.hs.backend.entity.ChefInfo::getLongitude)
                .isNotNull(com.hs.backend.entity.ChefInfo::getLatitude);

        List<ChefInfo> chefInfos = list(wrapper);

        // 计算距离并过滤（简化版，实际应该使用 Haversine 公式）
        return chefInfos.stream()
                .filter(chefInfo -> {
                    if (chefInfo.getLongitude() == null || chefInfo.getLatitude() == null) {
                        return false;
                    }
                    double distance = calculateDistance(longitude, latitude,
                            chefInfo.getLongitude(), chefInfo.getLatitude());
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
        if (status == 1) {
            chefInfo.setOnlineStatus(1); // 审核通过自动上架
        } else if (status == 2) {
            chefInfo.setOnlineStatus(0); // 审核拒绝自动下架
        }

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
     * 分页查询厨师审核列表（支持关键词搜索）
     */
    @Override
    public Page<ChefAuditVO> getChefAuditPage(Integer page, Integer size, String keyword, Integer auditStatus) {
        Page<ChefInfo> chefPage = new Page<>(page, size);

        LambdaQueryWrapper<ChefInfo> wrapper = new LambdaQueryWrapper<>();

        // 审核状态筛选
        if (auditStatus != null) {
            wrapper.eq(ChefInfo::getAuditStatus, auditStatus);
        }

        // 关键词搜索（按姓名或手机号）
        if (StringUtils.hasText(keyword)) {
            // 先搜索用户表获取匹配的 userId
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.like(User::getPhone, keyword);
            List<User> users = userMapper.selectList(userWrapper);
            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());

            // 如果关键词是数字，也尝试按 ID 搜索
            try {
                Long chefId = Long.parseLong(keyword);
                userIds.add(chefId);
            } catch (NumberFormatException e) {
                // 不是数字，忽略
            }

            if (!userIds.isEmpty()) {
                wrapper.in(ChefInfo::getUserId, userIds);
            }
        }

        // 按申请时间倒序
        wrapper.orderByDesc(ChefInfo::getCreateTime);

        // 执行分页查询
        Page<ChefInfo> chefInfoPage = page(chefPage, wrapper);

        // 转换为 ChefAuditVO
        Page<ChefAuditVO> voPage = new Page<>(page, size);
        List<ChefAuditVO> voList = chefInfoPage.getRecords().stream().map(chefInfo -> {
            ChefAuditVO vo = new ChefAuditVO();
            vo.setId(chefInfo.getId());
            vo.setUserId(chefInfo.getUserId());
            vo.setRealName(chefInfo.getRealName());
            vo.setApplyTime(chefInfo.getCreateTime());
            vo.setAuditStatus(chefInfo.getAuditStatus());

            // 从 User 表获取手机号
            User user = userMapper.selectById(chefInfo.getUserId());
            if (user != null) {
                vo.setPhone(user.getPhone());
            }

            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        voPage.setTotal(chefInfoPage.getTotal());
        voPage.setSize(chefInfoPage.getSize());
        voPage.setCurrent(chefInfoPage.getCurrent());

        return voPage;
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
