package com.hs.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 厨师专属信息表
 * @TableName t_chef_info
 */
@TableName(value ="t_chef_info")
@Data
public class ChefInfo {
    /**
     * 厨师信息 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户 ID（逻辑外键：t_user.phone）
     */
    private String userId;

    /**
     * 厨师真实姓名
     */


    private String realName;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 身份证号码（18位）
     */
    private String idCardNo;

    /**
     * 手机号（支持国际号码，预留长度）
     */
    private String phone;

    /**
     * 身份证正面照 URL（加长长度适配OSS地址）
     */
    private String idCardFront;

    /**
     * 身份证背面照 URL
     */
    private String idCardBack;

    /**
     * 详细地址（街道、门牌号等）
     */
    private String detailAddress;

    /**
     * 厨师头像 URL
     */
    private String avatarUrl;

    /**
     * 厨师资质证书 URL（如厨师证）
     */
    private String qualificationUrl;

    /**
     * 烹饪年限（无负数，用 unsigned）
     */
    private Integer experienceYears;
    
    /**
     * 厨师等级：1-初级厨师，2-中级厨师，3-高级厨师，4-资深厨师，5-特级厨师
     */
    private Integer chefLevel;
    
    /**
     * 最低服务价格（元）
     */
    private BigDecimal minPrice;
    
    /**
     * 已完成订单数（冗余字段，便于查询）
     */
    private Integer completedOrders;
    
    /**
     * 厨师简介
     */
    private String introduction;

    /**
     * 审核状态：0-待审核，1-审核通过，2-审核拒绝
     */
    private Integer auditStatus;

    /**
     * 审核备注（加长长度满足备注需求）
     */
    private String auditRemark;

    /**
     * 常驻位置纬度（-90~90，精度约1米）
     */
    private BigDecimal latitude;

    /**
     * 常驻位置经度（-180~180，精度约1米）
     */
    private BigDecimal longitude;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;


}