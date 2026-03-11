package com.hs.backend.controller;

import com.hs.backend.common.Result;
import com.hs.backend.common.service.OssService;
import com.hs.backend.common.config.OssConfig;
import com.hs.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 头像上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/avatar")
@RequiredArgsConstructor
public class AvatarController {
    
    private final OssService ossService;
   private final OssConfig ossConfig;
   private final UserService userService;
    
    /**
     * 上传头像（后端代理模式）
     * @param file 头像文件
     * @return 完整 URL
     */
    @PostMapping("/upload")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }
            
            // 验证文件大小（限制 5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.error("图片大小不能超过 5MB");
            }
            
            // 上传到 OSS
            String relativePath = ossService.uploadFile(file, "avatar");
            
            // 获取完整 URL
          String fullUrl = ossConfig.getFullUrl(relativePath);
            
            // TODO: 这里需要获取当前登录用户的 ID，然后保存到数据库
            // Long userId = SecurityContextHolder.getUserId();
            // userService.updateAvatar(userId, relativePath);
            
            Map<String, String> data = new HashMap<>();
            data.put("url", fullUrl);
            data.put("relativePath", relativePath);
            
            return Result.success("上传成功", data);
            
        } catch (IOException e) {
            log.error("头像上传失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取上传签名（前端直传模式）
     * @param filename 文件名
     * @return 签名 URL 和完整访问地址
     */
    @GetMapping("/signature")
    public Result<Map<String, String>> getUploadSignature(@RequestParam("filename") String filename) {
        try {
            // 生成唯一文件名
            String uniqueFilename = java.util.UUID.randomUUID().toString().replace("-", "") + 
                                   getFileExtension(filename);
            
            String objectKey = "avatar/" + uniqueFilename;
            
            // 生成签名 URL（有效期 10 分钟）
            java.net.URL signatureUrl = ossService.generateUploadSignature(objectKey, 10);
            
            // 获取完整访问 URL
          String fullUrl = ossConfig.getFullUrl(objectKey);
            
            Map<String, String> data = new HashMap<>();
            data.put("signatureUrl", signatureUrl.toString());
            data.put("fullUrl", fullUrl);
            data.put("relativePath", objectKey);
            
            return Result.success("获取签名成功", data);
            
        } catch (Exception e) {
            log.error("获取上传签名失败", e);
            return Result.error("获取签名失败：" + e.getMessage());
        }
    }
    
    /**
     * 保存头像路径（前端直传 OSS 后调用）
     * @param relativePath 相对路径
     * @return 成功消息
     */
    @PostMapping("/save")
    public Result<String> saveAvatar(@RequestParam("relativePath") String relativePath) {
        try {
            // TODO: 这里需要获取当前登录用户的 ID
            // Long userId = SecurityContextHolder.getUserId();
            // userService.updateAvatar(userId, relativePath);
            
            return Result.success("保存成功");
            
        } catch (Exception e) {
            log.error("保存头像失败", e);
            return Result.error("保存失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
