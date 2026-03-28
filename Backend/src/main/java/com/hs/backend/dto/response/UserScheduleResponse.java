package com.hs.backend.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户时间段占用情况响应
 */
@Data
public class UserScheduleResponse {
    /**
     * 日期
     */
    private LocalDate date;
    
    /**
     * 时间段列表
     */
    private List<TimeSlotStatus> timeSlots;
    
    /**
     * 时间段状态
     */
    @Data
    public static class TimeSlotStatus {
        /**
         * 时间段（如：11:00-13:00）
         */
        private String timeSlot;
        
        /**
         * 时间段状态：
         * 0 - 空闲（该时间段无订单）
         * 1 - 订单提交（等待厨师接单）
         * 2 - 待支付（厨师接单，等待用户支付）
         * 3 - 已支付（等待服务）
         * 4 - 服务完成
         * 5 - 退款中
         * 注意：数据库状态 4（订单取消）和 6（已退款）前端按空闲处理不显示
         */
        private Integer status;
        
        /**
         * 订单 ID（如果有）
         */
        private Long orderId;
    }
}
