package com.hs.backend.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 头像上传请求 DTO
 */
@Data
public class AvatarUploadRequest {

    /**
     * 头像文件
     */
    private MultipartFile file;
}
