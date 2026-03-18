package com.hs.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 收藏实体类
 */
@Data
@TableName("t_customer_favorite")
public class CustomerFavorite {
    
    /**
     * 收藏 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 客户用户 ID
     */
    private Long customerId;
    
    /**
     * 厨师用户 ID
     */
    private Long chefId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
