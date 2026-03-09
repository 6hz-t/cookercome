package wit.backendcooker.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName t_chef_info
 */
@TableName(value ="t_chef_info")
@Data
public class ChefInfo {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String idCardFront;

    private String idCardBack;

    private Integer experienceYears;

    private String introduction;

    private Integer auditStatus;

    private String auditRemark;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Integer serviceRadius;

    private BigDecimal pricePerMeal;

    private Integer serviceStatus;

    private Date createTime;

    private Date updateTime;

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
        ChefInfo other = (ChefInfo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getIdCardFront() == null ? other.getIdCardFront() == null : this.getIdCardFront().equals(other.getIdCardFront()))
            && (this.getIdCardBack() == null ? other.getIdCardBack() == null : this.getIdCardBack().equals(other.getIdCardBack()))
            && (this.getExperienceYears() == null ? other.getExperienceYears() == null : this.getExperienceYears().equals(other.getExperienceYears()))
            && (this.getIntroduction() == null ? other.getIntroduction() == null : this.getIntroduction().equals(other.getIntroduction()))
            && (this.getAuditStatus() == null ? other.getAuditStatus() == null : this.getAuditStatus().equals(other.getAuditStatus()))
            && (this.getAuditRemark() == null ? other.getAuditRemark() == null : this.getAuditRemark().equals(other.getAuditRemark()))
            && (this.getLatitude() == null ? other.getLatitude() == null : this.getLatitude().equals(other.getLatitude()))
            && (this.getLongitude() == null ? other.getLongitude() == null : this.getLongitude().equals(other.getLongitude()))
            && (this.getServiceRadius() == null ? other.getServiceRadius() == null : this.getServiceRadius().equals(other.getServiceRadius()))
            && (this.getPricePerMeal() == null ? other.getPricePerMeal() == null : this.getPricePerMeal().equals(other.getPricePerMeal()))
            && (this.getServiceStatus() == null ? other.getServiceStatus() == null : this.getServiceStatus().equals(other.getServiceStatus()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getIdCardFront() == null) ? 0 : getIdCardFront().hashCode());
        result = prime * result + ((getIdCardBack() == null) ? 0 : getIdCardBack().hashCode());
        result = prime * result + ((getExperienceYears() == null) ? 0 : getExperienceYears().hashCode());
        result = prime * result + ((getIntroduction() == null) ? 0 : getIntroduction().hashCode());
        result = prime * result + ((getAuditStatus() == null) ? 0 : getAuditStatus().hashCode());
        result = prime * result + ((getAuditRemark() == null) ? 0 : getAuditRemark().hashCode());
        result = prime * result + ((getLatitude() == null) ? 0 : getLatitude().hashCode());
        result = prime * result + ((getLongitude() == null) ? 0 : getLongitude().hashCode());
        result = prime * result + ((getServiceRadius() == null) ? 0 : getServiceRadius().hashCode());
        result = prime * result + ((getPricePerMeal() == null) ? 0 : getPricePerMeal().hashCode());
        result = prime * result + ((getServiceStatus() == null) ? 0 : getServiceStatus().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
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
        sb.append(", idCardFront=").append(idCardFront);
        sb.append(", idCardBack=").append(idCardBack);
        sb.append(", experienceYears=").append(experienceYears);
        sb.append(", introduction=").append(introduction);
        sb.append(", auditStatus=").append(auditStatus);
        sb.append(", auditRemark=").append(auditRemark);
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", serviceRadius=").append(serviceRadius);
        sb.append(", pricePerMeal=").append(pricePerMeal);
        sb.append(", serviceStatus=").append(serviceStatus);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}