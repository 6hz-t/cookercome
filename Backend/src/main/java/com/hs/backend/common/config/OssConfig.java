package com.hs.backend.common.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * OSS 配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssConfig {
    
    /**
     * OSS Endpoint
     */
   private String endpoint;
    
    /**
     * Access Key ID
     */
   private String accessKeyId;
    
    /**
     * Access Key Secret
     */
   private String accessKeySecret;
    
    /**
     * Bucket 名称
     */
   private String bucketName;
    
    /**
     * 自定义域名（用于 CDN 加速）
     */
   private String customDomain;
    
    /**
     * 头像存储目录
     */
   private String avatarDir = "avatar/";
    
    /**
     * 创建 OSS Client Bean
     */
   @Bean
  public OSS ossClient() {
      return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
   }
    
    /**
     * 获取完整 URL（优先使用自定义域名）
     */
  public String getFullUrl(String relativePath) {
       if (relativePath == null || relativePath.isEmpty()) {
          return null;
       }
        
       // 如果配置了自定义域名，使用自定义域名
       if (customDomain != null && !customDomain.isEmpty()) {
          return "https://" + customDomain + "/" + relativePath;
       }
        
       // 否则使用 OSS 默认域名
      return "https://" + bucketName + "." + endpoint.replace("https://", "").replace("http://", "") + "/" + relativePath;
   }
}
