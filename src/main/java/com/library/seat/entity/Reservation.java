package com.library.seat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 预约实体类
 */
@Data
@TableName("reservation")
public class Reservation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 预约编号 */
    private String reservationNo;
    
    /** 用户ID */
    private Long userId;
    
    /** 座位ID */
    private Long seatId;
    
    /** 自习室ID */
    private Long roomId;
    
    /** 开始时间 */
    private LocalDateTime startTime;
    
    /** 结束时间 */
    private LocalDateTime endTime;
    
    /** 状态 */
    private String status;
    
    /** 审核状态 */
    private String auditStatus;
    
    /** 审核回复 */
    private String auditReply;
    
    /** 审核人ID */
    private Long auditorId;

    /** 审核时间 */
    private LocalDateTime auditTime;

    /** 取消原因 */
    private String cancelReason;
    
    /** 删除标记 */
    @TableLogic
    private Integer deleted;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 关联字段（非数据库字段）
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String userPhone;
    @TableField(exist = false)
    private String seatCode;
    @TableField(exist = false)
    private String roomName;
    @TableField(exist = false)
    private String roomLocation;
}
