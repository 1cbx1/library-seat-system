package com.library.seat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 签到实体类
 */
@Data
@TableName("check_in")
public class CheckIn implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 预约ID */
    private Long reservationId;
    
    /** 用户ID */
    private Long userId;
    
    /** 签到时间 */
    private LocalDateTime checkInTime;
    
    /** 签到方式: manual-手动, qrcode-扫码 */
    private String checkInMethod;
    
    /** 状态: 0-迟到, 1-正常 */
    private Integer status;
    
    /** 删除标记 */
    @TableLogic
    private Integer deleted;
}
