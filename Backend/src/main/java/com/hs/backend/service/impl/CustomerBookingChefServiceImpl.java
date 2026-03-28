package com.hs.backend.service.impl;

import com.aliyun.oss.OSS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.backend.common.config.OssConfig;
import com.hs.backend.dto.response.CustomerBookingChefResponse;
import com.hs.backend.entity.ChefInfo;
import com.hs.backend.entity.User;
import com.hs.backend.mapper.ChefInfoMapper;
import com.hs.backend.service.CustomerBookingChefService;
import com.hs.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户预约厨师服务实现
 */
@Slf4j
@Service
public class CustomerBookingChefServiceImpl extends ServiceImpl<ChefInfoMapper, ChefInfo> implements CustomerBookingChefService {

    private final OSS ossClient;
    private final OssConfig ossConfig;
    private final UserService userService;

    public CustomerBookingChefServiceImpl(OSS ossClient, OssConfig ossConfig, UserService userService) {
        this.ossClient = ossClient;
        this.ossConfig = ossConfig;
        this.userService = userService;
    }

    @Override
    public Page<CustomerBookingChefResponse> getChefPage(Integer page, Integer size, String sortBy, String name) {
        Page<ChefInfo> chefPage = new Page<>(page, size);

        LambdaQueryWrapper<ChefInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChefInfo::getAuditStatus, 1)
                .eq(ChefInfo::getStatus, 1);

        if (StringUtils.hasText(name)) {
            wrapper.like(ChefInfo::getRealName, name);
        }

        if (StringUtils.hasText(sortBy)) {
            switch (sortBy) {
                case "level_desc":
                    wrapper.orderByDesc(ChefInfo::getChefLevel);
                    break;
                case "price_asc":
                    wrapper.orderByAsc(ChefInfo::getMinPrice);
                    break;
                case "orders_desc":
                    wrapper.orderByDesc(ChefInfo::getCompletedOrders);
                    break;
                default:
                    wrapper.orderByDesc(ChefInfo::getId);
            }
        } else {
            wrapper.orderByDesc(ChefInfo::getId);
        }

        Page<ChefInfo> resultPage = page(chefPage, wrapper);

        Page<CustomerBookingChefResponse> responsePage = new Page<>(page, size, resultPage.getTotal());
        List<CustomerBookingChefResponse> responses = resultPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        responsePage.setRecords(responses);

        return responsePage;
    }

    /**
     * 转换 ChefInfo 为响应对象，并生成头像可访问链接。
     */
    private CustomerBookingChefResponse convertToResponse(ChefInfo chefInfo) {
        CustomerBookingChefResponse response = new CustomerBookingChefResponse();
        BeanUtils.copyProperties(chefInfo, response);

        response.setId(Long.parseLong(chefInfo.getUserId()));

        if (chefInfo.getPhone() == null || chefInfo.getPhone().isEmpty()) {
            User user = userService.getById(chefInfo.getUserId());
            if (user != null) {
                response.setPhone(user.getPhone());
            }
        }

        String avatar = chefInfo.getAvatarUrl();
        if (avatar != null && !avatar.isEmpty() && !avatar.startsWith("http")) {
            try {
                Date expiration = new Date(System.currentTimeMillis() + 43200L * 60 * 1000);
                java.net.URL signedUrl = ossClient.generatePresignedUrl(
                        ossConfig.getBucketName(),
                        avatar,
                        expiration
                );
                response.setAvatarUrl(signedUrl.toString());
            } catch (Exception e) {
                log.warn("生成厨师头像签名 URL 失败，使用普通 URL：{}", e.getMessage());
                String fullUrl = "https://" + ossConfig.getBucketName() + "."
                        + ossConfig.getEndpoint().replace("https://", "").replace("http://", "")
                        + "/" + avatar;
                response.setAvatarUrl(fullUrl);
            }
        } else if (avatar != null && avatar.startsWith("http")) {
            response.setAvatarUrl(avatar);
        }

        return response;
    }

    @Override
    public void clearChefListCache() {
        log.info("当前已改为直接查询数据库，无需清理厨师列表缓存");
    }
}
