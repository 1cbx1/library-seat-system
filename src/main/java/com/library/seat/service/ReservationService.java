package com.library.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.seat.entity.Reservation;
import java.util.List;
import java.util.Map;

/**
 * 预约 Service 接口
 */
public interface ReservationService extends IService<Reservation> {
    
    /**
     * 创建预约
     */
    Reservation createReservation(Reservation reservation);
    
    /**
     * 用户预约列表
     */
    List<Reservation> getUserReservations(Long userId);
    
    /**
     * 获取所有预约列表（后台）
     */
    List<Reservation> getAllList();
    
    /**
     * 审核预约
     */
    boolean auditReservation(Long id, String status, String reply);
    
    /**
     * 取消预约
     */
    boolean cancelReservation(Long id, String reason);
    
    /**
     * 签到
     */
    boolean checkIn(Long reservationId);
    
    /**
     * 签退
     */
    boolean checkOut(Long reservationId);
    
    /**
     * 获取今日预约数
     */
    Long getTodayCount();
    
    /**
     * 获取活跃预约数
     */
    Long getActiveCount();
    
    /**
     * 获取自习室统计
     */
    List<Map<String, Object>> getRoomStats();
    
    /**
     * 获取用户统计
     */
    List<Map<String, Object>> getUserStats();
}
