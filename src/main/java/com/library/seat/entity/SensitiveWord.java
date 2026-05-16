package com.library.seat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 敏感词实体类
 */
@Data
@TableName("sensitive_word")
public class SensitiveWord implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 敏感词 */
    private String word;
    
    /** 类型: content-内容, nickname-昵称 */
    private String type;
    
    /** 状态: 0-禁用, 1-启用 */
    private Integer status;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
