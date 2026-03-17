package com.hs.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 厨师实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_chef_info")
public class ChefInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 厨师编号
     */
    private String chefNo;

    /**
     * 工作年限
     */
    private Integer workYears;

    /*
    * 厨师真实姓名
    * */
    private String realName;

    /**
     * 擅长菜系（逗号分隔）
     */
    private String specialty;

    /**
     * 厨师等级：1-初级，2-中级，3-高级，4-名厨
     */
    private Integer level;

    /**
     * 服务次数
     */
    private Integer serviceCount;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 起步价
     */
    private BigDecimal basePrice;

    /**
     * 服务半径（公里）
     */
    private Integer serviceRadius;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 资质证书图片（逗号分隔）
     */
    private String certificates;

    /**
     * 审核状态：0-待审核，1-通过，2-拒绝
     */
    private Integer auditStatus;

    /**
     * 上架状态：0-下架，1-上架
     */
    private Integer onlineStatus;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /*
    * 状态：0-禁用，1-启用
    * */
    private Integer status;

}
