package com.library.seat.controller;

import com.library.seat.common.Result;
import com.library.seat.entity.Banner;
import com.library.seat.entity.User;
import com.library.seat.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping("/list")
    public Result<?> getList() {
        List<Banner> list = bannerService.getActiveList();
        return Result.success(list);
    }

    @PostMapping
    public Result<?> add(@RequestBody Banner banner) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        boolean success = bannerService.addBanner(banner);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Banner banner) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        banner.setId(id);
        boolean success = bannerService.updateById(banner);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        boolean success = bannerService.removeById(id);
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
