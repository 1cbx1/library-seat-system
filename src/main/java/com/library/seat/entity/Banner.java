package com.library.seat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;

/**
 * 轮播图实体类
 */
@Data
@TableName("banner")
public class Banner implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 标题 */
    private String title;
    
    /** 图片URL */
    private String imageUrl;
    
    /** 跳转链接 */
    private String linkUrl;
    
    /** 排序 */
    private Integer sort;
    
    /** 状态: 0-禁用, 1-启用 */
    private Integer status;
    
    /** 删除标记 */
    @TableLogic
    private Integer deleted;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private java.time.LocalDateTime createTime;
}
