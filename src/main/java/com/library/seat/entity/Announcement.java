package com.library.seat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告实体类
 */
@Data
@TableName("announcement")
public class Announcement implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 标题 */
    private String title;
    
    /** 内容 */
    private String content;
    
    /** 发布者ID */
    private Long authorId;
    
    /** 状态: 0-隐藏, 1-显示 */
    private Integer status;
    
    /** 是否置顶 */
    private Integer top;
    
    /** 删除标记 */
    @TableLogic
    private Integer deleted;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 数据库表 announcement 中没有该字段；用于展示时由 SQL join/扩展逻辑填充。
     * 这里声明为非持久字段，避免 MyBatis-Plus 生成查询时带上 author_name 列。
     */
    @TableField(exist = false)
    private String authorName;
}
