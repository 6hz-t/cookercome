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
 * 客户预约厨师服务实现类
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

        // 构建查询条件
        LambdaQueryWrapper<ChefInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChefInfo::getAuditStatus, 1)  // 只查询已审核通过的
                .eq(ChefInfo::getStatus, 1); // 只查询已启用的

        // 按名字搜索（模糊匹配）
        if (StringUtils.hasText(name)) {
            wrapper.like(ChefInfo::getRealName, name);
        }

        // 排序逻辑
        if (StringUtils.hasText(sortBy)) {
            switch (sortBy) {
                case "level_desc":
                    // 按审核状态排序（替代原来的 level 字段）
                    wrapper.orderByDesc(ChefInfo::getAuditStatus);
                    break;
                default:
                    // 默认排序：按创建时间
                    wrapper.orderByDesc(ChefInfo::getCreateTime);
            }
        } else {
            // 默认排序
            wrapper.orderByDesc(ChefInfo::getCreateTime);
        }

        // 执行分页查询
        Page<ChefInfo> resultPage = page(chefPage, wrapper);

        // 转换为 CustomerBookingChefResponse 并设置头像预签名 URL
        Page<CustomerBookingChefResponse> responsePage = new Page<>(page, size, resultPage.getTotal());
        List<CustomerBookingChefResponse> responses = resultPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        responsePage.setRecords(responses);

        return responsePage;
    }

    /**
     * 转换 ChefInfo 为 CustomerBookingChefResponse，并生成头像预签名 URL
     */
    private CustomerBookingChefResponse convertToResponse(ChefInfo chefInfo) {
        CustomerBookingChefResponse response = new CustomerBookingChefResponse();
        BeanUtils.copyProperties(chefInfo, response);

        // 设置手机号（从 User 表获取）
        User user = userService.getById(chefInfo.getUserId());
        if (user != null) {
            response.setPhone(user.getPhone());
        }

        // 生成头像预签名 URL（使用 avatarUrl 字段）
        String avatar = chefInfo.getAvatarUrl();

        if (avatar != null && !avatar.isEmpty() && !avatar.startsWith("http")) {
            try {
                // 生成预签名 URL（有效期 30 天 = 43200 分钟）
                Date expiration = new Date(System.currentTimeMillis() + 43200L * 60 * 1000);
                java.net.URL signedUrl = ossClient.generatePresignedUrl(
                    ossConfig.getBucketName(),
                    avatar,
                    expiration
                );
                response.setAvatarUrl(signedUrl.toString());
                log.debug("生成厨师头像预签名 URL: {}", signedUrl.toString());
            } catch (Exception e) {
                log.warn("生成预签名 URL 失败，使用普通 URL: {}", e.getMessage());
                // 降级方案：使用普通 URL
                String fullUrl = "https://" + ossConfig.getBucketName() + "." +
                                ossConfig.getEndpoint().replace("https://", "").replace("http://", "") + "/" + avatar;
                response.setAvatarUrl(fullUrl);
            }
        } else if (avatar != null && avatar.startsWith("http")) {
            response.setAvatarUrl(avatar);
        }

        return response;
    }
}
