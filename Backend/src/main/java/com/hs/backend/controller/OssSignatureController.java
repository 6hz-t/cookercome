package com.hs.backend.controller;

import com.aliyun.oss.OSS;
import com.hs.backend.common.Result;
import com.hs.backend.common.config.OssConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * OSS 预签名 URL 控制器
 * 用于生成临时访问链接，让前端可以访问私有 OSS 中的文件
 */
@Slf4j
@RestController
@RequestMapping("/api/oss")
@RequiredArgsConstructor
public class OssSignatureController {

    private final OSS ossClient;
    private final OssConfig ossConfig;

    /**
     * 生成文件访问的预签名 URL
     * @param objectKey 文件在 OSS 中的路径（例如：avatar/7/xxx.jpg）
     * @param expireMinutes 有效期（分钟），默认 60 分钟
     * @return 包含签名 URL 的 Map
     */
    @GetMapping("/signature")
    public Result<Map<String, String>> getSignatureUrl(
            @RequestParam("objectKey") String objectKey,
            @RequestParam(value = "expireMinutes", defaultValue = "43200") Integer expireMinutes) {
        
        try {
            // 计算过期时间
            Date expiration = new Date(System.currentTimeMillis() + expireMinutes * 60 * 1000L);
            
            // 生成预签名 URL（GET 请求，用于下载/查看文件）
            java.net.URL signedUrl = ossClient.generatePresignedUrl(
                ossConfig.getBucketName(), 
                objectKey, 
                expiration
            );
            
            // 返回给前端
            Map<String, String> result = new HashMap<>();
            result.put("url", signedUrl.toString());
            result.put("expireTime", String.valueOf(expiration.getTime()));
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("生成预签名 URL 失败", e);
            return Result.error(500, "生成签名 URL 失败：" + e.getMessage());
        }
    }
}
