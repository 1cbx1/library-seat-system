package com.library.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.seat.common.BusinessException;
import com.library.seat.entity.Reservation;
import com.library.seat.entity.Seat;
import com.library.seat.entity.StudyRoom;
import com.library.seat.entity.User;
import com.library.seat.mapper.ReservationMapper;
import com.library.seat.service.ReservationService;
import com.library.seat.service.SeatService;
import com.library.seat.service.StudyRoomService;
import com.library.seat.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);
    private static final Integer SEAT_AVAILABLE = 1;

    @Autowired
    private UserService userService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private StudyRoomService studyRoomService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Reservation createReservation(Reservation reservation) {
        validateReservation(reservation);
        checkSeatConflict(reservation.getSeatId(), reservation.getStartTime(), reservation.getEndTime());
        checkUserActiveReservation(reservation.getUserId());

        User user = userService.getById(reservation.getUserId());
        if (user != null && user.getCreditScore() != null && user.getCreditScore() < 60) {
            throw new BusinessException("信用分低于60分，暂不能预约，请联系管理员");
        }

        reservation.setReservationNo("R" + System.currentTimeMillis()
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        reservation.setStatus("pending");
        reservation.setAuditStatus("pending");
        this.save(reservation);
        logger.info("Create reservation: no={}, userId={}, seatId={}",
                reservation.getReservationNo(), reservation.getUserId(), reservation.getSeatId());
        return reservation;
    }

    private void validateReservation(Reservation reservation) {
        if (reservation.getSeatId() == null || reservation.getRoomId() == null) {
            throw new BusinessException("请选择自习室和座位");
        }
        if (reservation.getStartTime() == null || reservation.getEndTime() == null) {
            throw new BusinessException("请选择预约时间");
        }
        if (!reservation.getEndTime().isAfter(reservation.getStartTime())) {
            throw new BusinessException("结束时间必须晚于开始时间");
        }
        Seat seat = seatService.getById(reservation.getSeatId());
        if (seat == null || !SEAT_AVAILABLE.equals(seat.getStatus())) {
            throw new BusinessException("该座位当前不可预约");
        }
    }

    private void checkSeatConflict(Long seatId, LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper<Reservation> wrapper = new QueryWrapper<>();
        wrapper.eq("seat_id", seatId);
        wrapper.in("status", "pending", "approved", "checked_in");
        wrapper.lt("start_time", endTime);
        wrapper.gt("end_time", startTime);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("该座位在所选时间段已被预约，请选择其他时间段");
        }
    }

    private void checkUserActiveReservation(Long userId) {
        QueryWrapper<Reservation> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.in("status", "pending", "approved", "checked_in");
        if (this.count(wrapper) > 0) {
            throw new BusinessException("您已有未完成预约，请完成后再预约");
        }
    }

    @Override
    public List<Reservation> getUserReservations(Long userId) {
        QueryWrapper<Reservation> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        List<Reservation> list = this.list(wrapper);
        fillReservationInfo(list);
        return list;
    }

    @Override
    public List<Reservation> getAllList() {
        QueryWrapper<Reservation> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        List<Reservation> list = this.list(wrapper);
        fillReservationInfo(list);
        return list;
    }

    private void fillReservationInfo(List<Reservation> list) {
        for (Reservation reservation : list) {
            if (reservation.getUserId() != null) {
                User user = userService.getById(reservation.getUserId());
                if (user != null) {
                    reservation.setUserName(user.getRealName() != null ? user.getRealName() : user.getUsername());
                    reservation.setUserPhone(user.getPhone());
                }
            }
            if (reservation.getRoomId() != null) {
                StudyRoom room = studyRoomService.getById(reservation.getRoomId());
                if (room != null) {
                    reservation.setRoomName(room.getRoomName());
                    reservation.setRoomLocation(room.getLocation());
                }
            }
            if (reservation.getSeatId() != null) {
                Seat seat = seatService.getById(reservation.getSeatId());
                if (seat != null) {
                    reservation.setSeatCode(seat.getSeatCode());
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditReservation(Long id, String status, String reply) {
        Reservation reservation = this.getById(id);
        if (reservation == null || status == null) {
            return false;
        }
        if (!"pending".equals(reservation.getStatus())) {
            return false;
        }
        if (!"approved".equals(status) && !"rejected".equals(status)) {
            throw new BusinessException("审核状态只能为 approved 或 rejected");
        }

        reservation.setAuditStatus(status);
        reservation.setAuditReply(reply);
        reservation.setStatus(status);
        reservation.setAuditTime(LocalDateTime.now());

        if ("approved".equals(status) && !seatService.lockSeat(reservation.getSeatId())) {
            throw new BusinessException("座位状态已变化，无法审核通过");
        }
        boolean success = this.updateById(reservation);
        logger.info("Audit reservation: no={}, status={}", reservation.getReservationNo(), status);
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelReservation(Long id, String reason) {
        Reservation reservation = this.getById(id);
        if (reservation == null) {
            return false;
        }
        if ("completed".equals(reservation.getStatus()) || "cancelled".equals(reservation.getStatus())) {
            return false;
        }

        String oldStatus = reservation.getStatus();
        reservation.setCancelReason(reason);
        reservation.setStatus("cancelled");
        if ("pending".equals(reservation.getAuditStatus())) {
            reservation.setAuditStatus("cancelled");
        }
        if ("approved".equals(oldStatus) || "checked_in".equals(oldStatus)) {
            seatService.unlockSeat(reservation.getSeatId());
        }

        boolean success = this.updateById(reservation);
        logger.info("Cancel reservation: no={}, reason={}", reservation.getReservationNo(), reason);
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkIn(Long reservationId) {
        Reservation reservation = this.getById(reservationId);
        if (reservation == null || !"approved".equals(reservation.getStatus())) {
            return false;
        }
        if (!seatService.occupySeat(reservation.getSeatId())) {
            throw new BusinessException("座位状态异常，签到失败");
        }
        reservation.setStatus("checked_in");
        boolean success = this.updateById(reservation);
        logger.info("Check in: no={}, userId={}", reservation.getReservationNo(), reservation.getUserId());
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkOut(Long reservationId) {
        Reservation reservation = this.getById(reservationId);
        if (reservation == null || !"checked_in".equals(reservation.getStatus())) {
            return false;
        }
        reservation.setStatus("completed");
        seatService.unlockSeat(reservation.getSeatId());
        boolean success = this.updateById(reservation);
        logger.info("Check out: no={}, userId={}", reservation.getReservationNo(), reservation.getUserId());
        return success;
    }

    @Override
    public Long getTodayCount() {
        QueryWrapper<Reservation> wrapper = new QueryWrapper<>();
        wrapper.ge("create_time", LocalDate.now().atStartOfDay());
        return this.count(wrapper);
    }

    @Override
    public Long getActiveCount() {
        QueryWrapper<Reservation> wrapper = new QueryWrapper<>();
        wrapper.in("status", "approved", "checked_in");
        return this.count(wrapper);
    }

    @Override
    public List<Map<String, Object>> getRoomStats() {
        return this.baseMapper.getRoomStats();
    }

    @Override
    public List<Map<String, Object>> getUserStats() {
        return this.baseMapper.getUserStats();
    }
}
