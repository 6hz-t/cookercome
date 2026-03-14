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
            
            // 生成完整 URL
            String fullUrl = ossConfig.getFullUrl(relativePath);
            
            Map<String, String> result = new HashMap<>();
            result.put("relativePath", relativePath);
            result.put("fullUrl", fullUrl);
            
            log.info("文件上传成功：{}, 完整 URL: {}", relativePath, fullUrl);
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
            
            log.info("文件上传成功：{}", objectKey);
            return objectKey;
            
        } catch (IOException e) {
            log.error("文件上传失败：{}", objectKey, e);
            throw e;
        }
    }

    /**
     * 获取上传签名 URL（用于前端直传）
     * @param filename 文件名（包含目录）
     * @param expirationMinutes 过期时间（分钟）
     * @return 签名后的 PUT URL
     */
   public URL generateUploadSignature(String filename, int expirationMinutes) {
        // 生成签名 URL（允许前端在指定时间内上传）
        Date expiration= new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000L);
        
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
            ossConfig.getBucketName(), 
            filename
        );
       request.setExpiration(expiration);
        
        URL signatureUrl = ossClient.generatePresignedUrl(request);
       log.info("生成上传签名：{}", signatureUrl.toString());
        
       return signatureUrl;
    }

    /**
     * 获取上传签名信息（包含签名 URL、完整 URL、相对路径）
     * @param fileName 原始文件名
     * @param userId 用户 ID
     * @param expirationMinutes 过期时间（分钟）
     * @return 签名信息 Map
     */
    public Map<String, String> getUploadSignatureInfo(String fileName, Long userId, int expirationMinutes) {
        try {
            // 生成唯一的文件路径（相对路径）
            String relativePath = generateRelativePath(fileName, userId);
            
            // 生成完整 URL（用于前端显示）
            String fullUrl = ossConfig.getFullUrl(relativePath);
            
            // 生成带签名的上传 URL（有效期默认 5 分钟）
            URL signatureUrl = generateUploadSignature(relativePath, expirationMinutes);
            
            Map<String, String> result = new HashMap<>();
            result.put("signatureUrl", signatureUrl.toString());
            result.put("fullUrl", fullUrl);
            result.put("relativePath", relativePath);
            
            log.info("OSS 上传签名生成成功 - 相对路径：{}, 完整 URL: {}", relativePath, fullUrl);
            return result;
            
        } catch (Exception e) {
            log.error("生成 OSS 上传签名失败", e);
            throw new RuntimeException("生成上传签名失败：" + e.getMessage());
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
           log.info("文件删除成功：{}", objectKey);
        } catch (Exception e) {
           log.error("文件删除失败：{}", objectKey, e);
        }
    }
}
