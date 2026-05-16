package com.library.seat.controller;

import com.library.seat.common.Result;
import com.library.seat.entity.Reservation;
import com.library.seat.entity.User;
import com.library.seat.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 预约 Controller
 */
@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * 创建预约
     */
    @PostMapping
    public Result<?> createReservation(@RequestBody Reservation reservation) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        reservation.setUserId(user.getId());
        Reservation result = reservationService.createReservation(reservation);
        return Result.success("预约成功", result);
    }

    /**
     * 我的预约列表
     */
    @GetMapping("/my")
    public Result<?> getMyReservations() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        List<Reservation> list = reservationService.getUserReservations(user.getId());
        return Result.success(list);
    }

    /**
     * 取消预约
     */
    @PostMapping("/cancel/{id}")
    public Result<?> cancelReservation(@PathVariable Long id,
                                        @RequestParam(required = false) String reason) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        // 权限校验：只能取消自己的预约
        Reservation reservation = reservationService.getById(id);
        if (reservation == null) {
            return Result.error("预约不存在");
        }
        if (!reservation.getUserId().equals(user.getId())) {
            return Result.error(403, "无权操作他人的预约");
        }
        boolean success = reservationService.cancelReservation(id, reason);
        if (!success) {
            return Result.error("取消失败");
        }
        return Result.success("取消成功");
    }

    /**
     * 签到
     */
    @PostMapping("/checkin/{id}")
    public Result<?> checkIn(@PathVariable Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        // 权限校验：只能签到自己预约的座位
        Reservation reservation = reservationService.getById(id);
        if (reservation == null) {
            return Result.error("预约不存在");
        }
        if (!reservation.getUserId().equals(user.getId())) {
            return Result.error(403, "无权操作他人的预约");
        }
        boolean success = reservationService.checkIn(id);
        if (!success) {
            return Result.error("签到失败");
        }
        return Result.success("签到成功");
    }

    /**
     * 签退
     */
    @PostMapping("/checkout/{id}")
    public Result<?> checkOut(@PathVariable Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        // 权限校验：只能签退自己的预约
        Reservation reservation = reservationService.getById(id);
        if (reservation == null) {
            return Result.error("预约不存在");
        }
        if (!reservation.getUserId().equals(user.getId())) {
            return Result.error(403, "无权操作他人的预约");
        }
        boolean success = reservationService.checkOut(id);
        if (!success) {
            return Result.error("签退失败");
        }
        return Result.success("签退成功");
    }
}
