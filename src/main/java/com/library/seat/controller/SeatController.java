package com.library.seat.controller;

import com.library.seat.common.Result;
import com.library.seat.entity.Seat;
import com.library.seat.entity.User;
import com.library.seat.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

/**
 * 座位 Controller
 */
@RestController
@RequestMapping("/api/seat")
public class SeatController {

    @Autowired
    private SeatService seatService;

    /**
     * 获取自习室的座位列表
     */
    @GetMapping("/room/{roomId}")
    public Result<?> getByRoom(@PathVariable Long roomId) {
        List<Seat> list = seatService.getByRoomId(roomId);
        return Result.success(list);
    }

    /**
     * 获取座位详情
     */
    @GetMapping("/{id}")
    public Result<?> getDetail(@PathVariable Long id) {
        Seat seat = seatService.getById(id);
        return seat != null ? Result.success(seat) : Result.error("座位不存在");
    }

    // ==================== 后台管理API ====================

    /**
     * 获取所有座位
     */
    @GetMapping("/admin/list")
    public Result<?> getAdminList() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null || !"admin".equals(user.getRole())) {
            return Result.error(403, "无权限访问");
        }
        List<Seat> list = seatService.getAllSeats();
        return Result.success(list);
    }

    /**
     * 添加座位
     */
    @PostMapping
    public Result<?> addSeat(@RequestBody Seat seat) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null || !"admin".equals(user.getRole())) {
            return Result.error(403, "无权限访问");
        }
        boolean success = seatService.addSeat(seat);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 更新座位
     */
    @PutMapping("/{id}")
    public Result<?> updateSeat(@PathVariable Long id, @RequestBody Seat seat) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null || !"admin".equals(user.getRole())) {
            return Result.error(403, "无权限访问");
        }
        seat.setId(id);
        boolean success = seatService.updateSeat(seat);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除座位
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteSeat(@PathVariable Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null || !"admin".equals(user.getRole())) {
            return Result.error(403, "无权限访问");
        }
        boolean success = seatService.deleteSeat(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
