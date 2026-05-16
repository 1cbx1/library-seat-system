package com.library.seat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 签退实体类
 */
@Data
@TableName("check_out")
public class CheckOut implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 预约ID */
    private Long reservationId;
    
    /** 用户ID */
    private Long userId;
    
    /** 签退时间 */
    private LocalDateTime checkOutTime;
    
    /** 状态: 0-早退, 1-正常 */
    private Integer status;
    
    /** 删除标记 */
    @TableLogic
    private Integer deleted;
}
