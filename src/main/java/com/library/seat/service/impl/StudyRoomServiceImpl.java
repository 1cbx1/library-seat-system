package com.library.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.seat.entity.StudyRoom;
import com.library.seat.mapper.StudyRoomMapper;
import com.library.seat.service.StudyRoomService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 自习室 Service 实现
 */
@Service
public class StudyRoomServiceImpl extends ServiceImpl<StudyRoomMapper, StudyRoom> implements StudyRoomService {

    @Override
    public List<StudyRoom> getOpenRooms() {
        QueryWrapper<StudyRoom> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByDesc("create_time");
        return this.list(wrapper);
    }

    @Override
    public StudyRoom getRoomDetail(Long id) {
        return this.getById(id);
    }

    @Override
    public List<StudyRoom> getAllRooms() {
        QueryWrapper<StudyRoom> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return this.list(wrapper);
    }

    @Override
    public boolean addRoom(StudyRoom room) {
        // 生成自习室编号
        if (room.getRoomCode() == null || room.getRoomCode().isEmpty()) {
            room.setRoomCode("R" + System.currentTimeMillis());
        }
        if (room.getStatus() == null) {
            room.setStatus(1);
        }
        return this.save(room);
    }

    @Override
    public boolean updateRoom(StudyRoom room) {
        return this.updateById(room);
    }

    @Override
    public boolean deleteRoom(Long id) {
        return this.removeById(id);
    }
}
