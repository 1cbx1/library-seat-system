package com.library.seat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 论坛帖子实体类
 */
@Data
@TableName("forum_post")
public class ForumPost implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 标题 */
    private String title;
    
    /** 内容 */
    private String content;
    
    /** 发布者ID */
    private Long userId;
    
    /** 标签 */
    private String tag;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 浏览数 */
    private Integer viewCount;
    
    /** 收藏数 */
    private Integer collectCount;
    
    /** 评论数 */
    private Integer commentCount;
    
    /** 状态: 0-隐藏, 1-正常 */
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
    @TableField(exist = false)
    private String userAvatar;
}
