package com.library.seat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 自习室实体类
 */
@Data
@TableName("study_room")
public class StudyRoom implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 自习室ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 自习室编号 */
    private String roomCode;
    
    /** 自习室名称 */
    private String roomName;
    
    /** 位置 */
    private String location;
    
    /** 楼层 */
    private String floor;
    
    /** 座位容量 */
    private Integer capacity;
    
    /** 小时单价 */
    private BigDecimal hourPrice;
    
    /** 开放时间 */
    private String openTime;
    
    /** 状态: 0-关闭, 1-开放 */
    private Integer status;
    
    /** 图片 */
    private String imageUrl;
    
    /** 描述 */
    private String description;
    
    /** 删除标记 */
    @TableLogic
    private Integer deleted;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
