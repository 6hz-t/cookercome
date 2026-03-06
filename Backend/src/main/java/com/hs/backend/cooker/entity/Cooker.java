package com.hs.backend.cooker.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

/**
 * 厨师实体类
 */
@Data
@Document(collection = "cooker")
public class Cooker {
    
    @Id
    private String id;
    
    /** 用户名 */
    private String username;
    
    /** 密码（加密） */
    private String password;
    
    /** 手机号 */
    private String phone;
    
    /** 身份证号 */
    private String idCard;
    
    /** 真实姓名 */
    private String realName;
    
    /** 头像 URL */
    private String avatar;
    
    /** 简介 */
    private String introduction;
    
    /** 擅长菜系 */
    private String[] dishKinds;
    
    /** 评分 */
    private Double star;
    
    /** 状态：0-审核中 1-正常 2-禁用 */
    private Integer status;
    
    /** 证书图片 URL */
    private String[] certificates;
    
    /** 创建时间 */
    private Date createTime;
    
    /** 更新时间 */
    private Date updateTime;
}
