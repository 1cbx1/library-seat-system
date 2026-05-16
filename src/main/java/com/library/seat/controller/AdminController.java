package com.library.seat.controller;

import com.library.seat.common.Result;
import com.library.seat.entity.Reservation;
import com.library.seat.entity.User;
import com.library.seat.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/reservation/list")
    public Result<?> getReservationList() {
        User admin = currentUser();
        if (!isAdmin(admin)) {
            return Result.error(403, "无权限访问");
        }
        List<Reservation> list = reservationService.getAllList();
        return Result.success(list);
    }

    @PostMapping("/reservation/audit/{id}")
    public Result<?> auditReservation(@PathVariable Long id, @RequestBody Map<String, String> params) {
        User admin = currentUser();
        if (!isAdmin(admin)) {
            return Result.error(403, "无权限访问");
        }
        String status = params.get("status");
        String reply = params.getOrDefault("reply", "");
        boolean success = reservationService.auditReservation(id, status, reply);
        return success ? Result.success("审核成功") : Result.error("审核失败");
    }

    @PutMapping("/reservation/audit/{id}")
    public Result<?> auditReservationByPut(@PathVariable Long id,
                                           @RequestParam String status,
                                           @RequestParam(required = false, defaultValue = "") String reply) {
        User admin = currentUser();
        if (!isAdmin(admin)) {
            return Result.error(403, "无权限访问");
        }
        boolean success = reservationService.auditReservation(id, status, reply);
        return success ? Result.success("审核成功") : Result.error("审核失败");
    }

    @PostMapping("/reservation/cancel/{id}")
    public Result<?> cancelReservation(@PathVariable Long id,
                                       @RequestParam(required = false, defaultValue = "管理员取消") String reason) {
        User admin = currentUser();
        if (!isAdmin(admin)) {
            return Result.error(403, "无权限访问");
        }
        boolean success = reservationService.cancelReservation(id, reason);
        return success ? Result.success("取消成功") : Result.error("取消失败");
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return null;
        }
        return (User) authentication.getPrincipal();
    }

    private boolean isAdmin(User user) {
        return user != null && "admin".equals(user.getRole());
    }
}
