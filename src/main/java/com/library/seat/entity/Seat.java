package com.library.seat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 座位实体类
 */
@Data
@TableName("seat")
public class Seat implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 座位ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 座位编号 */
    private String seatCode;
    
    /** 所属自习室ID */
    private Long roomId;
    
    /** 状态: 0-维修中, 1-可用 */
    private Integer status;
    
    /** 是否有电源: 0-无, 1-有 */
    private Integer hasPower;
    
    /** 是否靠窗: 0-否, 1-是 */
    private Integer hasWindow;
    
    /** 删除标记 */
    @TableLogic
    private Integer deleted;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    // 关联自习室信息（非数据库字段）
    @TableField(exist = false)
    private String roomName;
    @TableField(exist = false)
    private String location;
}
