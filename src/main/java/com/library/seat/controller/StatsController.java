package com.library.seat.controller;

import com.library.seat.common.Result;
import com.library.seat.entity.User;
import com.library.seat.service.ReservationService;
import com.library.seat.service.SeatService;
import com.library.seat.service.StudyRoomService;
import com.library.seat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
public class StatsController {

    @Autowired
    private StudyRoomService studyRoomService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @GetMapping
    public Result<?> getStats() {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        Map<String, Object> stats = new HashMap<>();
        stats.put("roomCount", studyRoomService.count());
        stats.put("seatCount", seatService.count());
        stats.put("reservationCount", reservationService.count());
        stats.put("userCount", userService.count());
        stats.put("todayReservation", reservationService.getTodayCount());
        stats.put("todayCount", reservationService.getTodayCount());
        stats.put("activeReservation", reservationService.getActiveCount());
        stats.put("activeCount", reservationService.getActiveCount());
        return Result.success(stats);
    }

    @GetMapping("/charts")
    public Result<?> getCharts() {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        Map<String, Object> charts = new HashMap<>();
        charts.put("roomStats", reservationService.getRoomStats());
        charts.put("userStats", reservationService.getUserStats());
        return Result.success(charts);
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return false;
        }
        User user = (User) authentication.getPrincipal();
        return "admin".equals(user.getRole());
    }
}
