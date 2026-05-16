package com.library.seat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 留言板实体类
 */
@Data
@TableName("message_board")
public class MessageBoard implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 标题 */
    private String title;
    
    /** 内容 */
    private String content;
    
    /** 留言者ID */
    private Long userId;
    
    /** 回复内容 */
    private String replyContent;
    
    /** 回复人ID */
    private Long replyId;
    
    /** 状态: 0-待回复, 1-已回复 */
    private Integer status;
    
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
}
