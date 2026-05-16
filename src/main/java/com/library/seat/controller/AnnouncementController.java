package com.library.seat.controller;

import com.library.seat.common.Result;
import com.library.seat.entity.Announcement;
import com.library.seat.entity.User;
import com.library.seat.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/list")
    public Result<?> getList() {
        List<Announcement> list = announcementService.getActiveList();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<?> getDetail(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        return Result.success(announcement);
    }

    @PostMapping
    public Result<?> publish(@RequestBody Announcement announcement) {
        User admin = currentUser();
        if (!isAdmin(admin)) {
            return Result.error(403, "无权限访问");
        }
        announcement.setAuthorId(admin.getId());
        boolean success = announcementService.publish(announcement);
        return success ? Result.success("发布成功") : Result.error("发布失败");
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Announcement announcement) {
        if (!isAdmin(currentUser())) {
            return Result.error(403, "无权限访问");
        }
        announcement.setId(id);
        boolean success = announcementService.updateById(announcement);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (!isAdmin(currentUser())) {
            return Result.error(403, "无权限访问");
        }
        boolean success = announcementService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PostMapping("/{id}/top")
    public Result<?> setTop(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        if (!isAdmin(currentUser())) {
            return Result.error(403, "无权限访问");
        }
        Integer top = params.getOrDefault("top", 0);
        boolean success = announcementService.setTop(id, top);
        return success ? Result.success("操作成功") : Result.error("操作失败");
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
