package com.library.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.seat.entity.StudyRoom;
import java.util.List;

/**
 * 自习室 Service 接口
 */
public interface StudyRoomService extends IService<StudyRoom> {
    
    /**
     * 获取开放的自习室列表
     */
    List<StudyRoom> getOpenRooms();
    
    /**
     * 自习室详情
     */
    StudyRoom getRoomDetail(Long id);
    
    /**
     * 获取所有自习室（包括关闭的）
     */
    List<StudyRoom> getAllRooms();
    
    /**
     * 添加自习室
     */
    boolean addRoom(StudyRoom room);
    
    /**
     * 更新自习室
     */
    boolean updateRoom(StudyRoom room);
    
    /**
     * 删除自习室
     */
    boolean deleteRoom(Long id);
}
