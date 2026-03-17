package com.hs.backend.common.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.CannedAccessControlList;
import com.hs.backend.common.config.OssConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * OSS 服务类
 * 负责阿里云 OSS 文件上传、删除等操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OssService {
    
    private final OSS ossClient;
    private final OssConfig ossConfig;
    
    /**
     * 上传头像到 OSS（专用方法）
     * @param file 头像文件
     * @param userId 用户 ID
     * @return 包含相对路径和完整 URL 的 Map
     */
    public Map<String, String> uploadAvatar(MultipartFile file, Long userId) throws IOException {
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只能上传图片文件");
        }
        
        // 验证文件大小（5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("图片大小不能超过 5MB");
        }
        
        // 使用通用的 uploadFile 方法上传
        return uploadFile(file, userId, ossConfig.getAvatarDir());
    }
    
    /**
     * 上传文件到 OSS（后端代理模式，包含完整 URL）
     * @param file 文件
     * @param userId 用户 ID
     * @param dir 目录
     * @return 包含相对路径和完整 URL 的 Map
     */
    public Map<String, String> uploadFile(MultipartFile file, Long userId, String dir) throws IOException {
        // 生成唯一的文件路径（相对路径）
        String relativePath = generateRelativePath(file.getOriginalFilename(), userId);
        
        try {
            // 创建文件元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            
            // 上传文件（带元数据）
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                ossConfig.getBucketName(), 
                relativePath, 
                file.getInputStream(),
                metadata
            );
            
            ossClient.putObject(putObjectRequest);
            
            String fullUrl = ossConfig.getFullUrl(relativePath);
            
            Map<String, String> result = new HashMap<>();
            result.put("relativePath", relativePath);
            result.put("fullUrl", fullUrl);
            
            return result;
            
        } catch (IOException e) {
            log.error("文件上传失败：{}", relativePath, e);
            throw e;
        }
    }
    
    /**
     * 上传文件到 OSS（后端代理模式 - 旧版本，保持兼容）
     * @param file 文件
     * @param dir 目录
     * @return 相对路径
     */
   public String uploadFileToDir(MultipartFile file, String dir) throws IOException {
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + extension;
        
        // 完整对象名（目录 + 文件名）
        String objectKey = dir + "/" + filename;
        
        try {
            // 创建文件元数据
            ObjectMetadata metadata = new ObjectMetadata();
            
            // 上传文件（带元数据）
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                ossConfig.getBucketName(), 
                objectKey, 
                file.getInputStream(),
                metadata
            );
            
            ossClient.putObject(putObjectRequest);
            
            return objectKey;
            
        } catch (IOException e) {
            log.error("文件上传失败：{}", objectKey, e);
            throw e;
        }
    }

    /**
     * 生成相对路径
     * 格式：avatar/{userId}/{timestamp}_{uuid}_{filename}
     */
    private String generateRelativePath(String fileName, Long userId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String uniqueFileName = timestamp + "_" + uuid + "_" + fileName;
        return ossConfig.getAvatarDir() + userId + "/" + uniqueFileName;
    }
    
    /**
     * 删除文件
     * @param objectKey 对象键（相对路径）
     */
   public void deleteFile(String objectKey) {
        try {
            ossClient.deleteObject(ossConfig.getBucketName(), objectKey);
        } catch (Exception e) {
            log.error("文件删除失败：{}", objectKey, e);
        }
    }
}
