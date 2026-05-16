package com.library.seat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.seat.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

    @Select("select * from reservation where deleted = 0 and user_id = #{userId} order by create_time desc")
    List<Reservation> selectByUserId(@Param("userId") Long userId);

    @Select("select * from reservation where deleted = 0 and seat_id = #{seatId} " +
            "and status in ('pending','approved','checked_in') " +
            "and start_time < #{endTime} and end_time > #{startTime}")
    List<Reservation> selectConflict(@Param("seatId") Long seatId,
                                     @Param("startTime") String startTime,
                                     @Param("endTime") String endTime);

    @Select("select sr.room_name as name, count(r.id) as value " +
            "from study_room sr left join reservation r on sr.id = r.room_id and r.deleted = 0 " +
            "where sr.deleted = 0 group by sr.id, sr.room_name order by sr.id")
    List<Map<String, Object>> getRoomStats();

    @Select("select date_format(create_time, '%Y-%m-%d') as date, count(*) as count " +
            "from reservation where deleted = 0 and create_time >= date_sub(curdate(), interval 6 day) " +
            "group by date_format(create_time, '%Y-%m-%d') order by date")
    List<Map<String, Object>> getUserStats();
}
