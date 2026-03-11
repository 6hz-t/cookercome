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
     * 上传文件到 OSS（后端代理模式）
     * @param file 文件
     * @param dir 目录
     * @return 相对路径
     */
   public String uploadFile(MultipartFile file, String dir) throws IOException {
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
            // 设置公共读权限（允许前端直接访问）
           metadata.setObjectAcl(CannedAccessControlList.PublicRead);
            
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
