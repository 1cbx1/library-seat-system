package com.library.seat.controller;

import com.library.seat.common.Result;
import com.library.seat.entity.StudyRoom;
import com.library.seat.entity.User;
import com.library.seat.service.StudyRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room")
public class StudyRoomController {

    @Autowired
    private StudyRoomService studyRoomService;

    @GetMapping("/list")
    public Result<?> getRoomList() {
        List<StudyRoom> list = studyRoomService.getOpenRooms();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<?> getRoomDetail(@PathVariable Long id) {
        StudyRoom room = studyRoomService.getRoomDetail(id);
        return room != null ? Result.success(room) : Result.error("自习室不存在");
    }

    @GetMapping("/admin/list")
    public Result<?> getAdminRoomList() {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        List<StudyRoom> list = studyRoomService.getAllRooms();
        return Result.success(list);
    }

    @PostMapping
    public Result<?> addRoom(@RequestBody StudyRoom room) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        boolean success = studyRoomService.addRoom(room);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping("/{id}")
    public Result<?> updateRoom(@PathVariable Long id, @RequestBody StudyRoom room) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        room.setId(id);
        boolean success = studyRoomService.updateRoom(room);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteRoom(@PathVariable Long id) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        boolean success = studyRoomService.deleteRoom(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
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
