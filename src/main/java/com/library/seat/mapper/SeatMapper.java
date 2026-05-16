package com.library.seat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.seat.entity.Seat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 座位 Mapper
 */
@Mapper
public interface SeatMapper extends BaseMapper<Seat> {
    
    /**
     * 根据自习室ID查询座位列表
     */
    List<Seat> selectByRoomId(@Param("roomId") Long roomId);
}
