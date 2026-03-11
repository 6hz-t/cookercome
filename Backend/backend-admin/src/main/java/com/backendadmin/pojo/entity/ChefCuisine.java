package com.admin.backendadmin.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 厨师 - 菜系关联表（多对多）
 * @TableName t_chef_cuisine
 */
@TableName(value ="t_chef_cuisine")
@Data
public class ChefCuisine {
    /**
     * 关联 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 厨师用户 ID（逻辑外键：t_user.id）
     */
    private Long userId;

    /**
     * 菜系 ID（逻辑外键：t_cuisine.id）
     */
    private Integer cuisineId;

    /**
     * 是否主打菜系：1-是，0-否
     */
    private Integer isMain;

    /**
     * 创建时间
     */
    private Date createTime;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ChefCuisine other = (ChefCuisine) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getCuisineId() == null ? other.getCuisineId() == null : this.getCuisineId().equals(other.getCuisineId()))
            && (this.getIsMain() == null ? other.getIsMain() == null : this.getIsMain().equals(other.getIsMain()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getCuisineId() == null) ? 0 : getCuisineId().hashCode());
        result = prime * result + ((getIsMain() == null) ? 0 : getIsMain().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", cuisineId=").append(cuisineId);
        sb.append(", isMain=").append(isMain);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}