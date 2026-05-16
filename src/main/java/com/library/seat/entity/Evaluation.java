package com.library.seat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评价实体类
 */
@Data
@TableName("evaluation")
public class Evaluation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 预约ID */
    private Long reservationId;
    
    /** 用户ID */
    private Long userId;
    
    /** 自习室ID */
    private Long roomId;
    
    /** 评分 1-5 */
    private Integer score;
    
    /** 评价内容 */
    private String content;
    
    /** 删除标记 */
    @TableLogic
    private Integer deleted;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    // 关联字段（非数据库字段）
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String roomName;
}
