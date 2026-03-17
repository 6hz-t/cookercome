package com.hs.backend.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 厨师审核列表 VO
 */
@Data
public class ChefAuditVO {

    /**
     * 厨师 ID
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 厨师姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 申请时间（创建时间）
     */
    private LocalDateTime applyTime;

    /**
     * 审核状态：0-待审核，1-通过，2-拒绝
     */
    private Integer auditStatus;

    /**
     * 拒绝原因
     */
    private String rejectReason;
}
