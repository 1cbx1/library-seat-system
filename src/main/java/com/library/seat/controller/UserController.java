package com.library.seat.controller;

import com.library.seat.common.JwtUtil;
import com.library.seat.common.LoginLimitService;
import com.library.seat.common.Result;
import com.library.seat.entity.User;
import com.library.seat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginLimitService loginLimitService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final int MAX_FAIL_COUNT = 5;

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody User loginRequest,
                           @RequestParam(defaultValue = "false") boolean remember) {
        if (loginLimitService.isLocked(loginRequest.getUsername())) {
            long minutes = loginLimitService.getLockRemainingMinutes(loginRequest.getUsername());
            return Result.error(423, "账号已锁定，请 " + minutes + " 分钟后再试");
        }

        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (user == null) {
            loginLimitService.recordFail(loginRequest.getUsername());
            int remaining = loginLimitService.getRemainingAttempts(loginRequest.getUsername());
            if (remaining < MAX_FAIL_COUNT) {
                return Result.error("用户名或密码错误，剩余尝试次数：" + remaining);
            }
            return Result.error("用户名或密码错误");
        }

        loginLimitService.recordSuccess(loginRequest.getUsername());
        if (user.getStatus() != null && user.getStatus() == 0) {
            return Result.error(403, "账号已被禁用，请联系管理员");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", user);
        return Result.success(result);
    }

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody User user) {
        boolean success = userService.register(user);
        return success ? Result.success("注册成功") : Result.error("用户名已存在");
    }

    @GetMapping("/info")
    public Result<?> getUserInfo() {
        User user = currentUser();
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success(userService.getById(user.getId()));
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody User payload) {
        User currentUser = currentUser();
        if (currentUser == null) {
            return Result.error(401, "请先登录");
        }
        User user = userService.getById(currentUser.getId());
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setRealName(payload.getRealName());
        user.setPhone(payload.getPhone());
        user.setEmail(payload.getEmail());
        boolean success = userService.updateUser(user);
        return success ? Result.success("资料更新成功") : Result.error("资料更新失败");
    }

    @PostMapping("/logout")
    public Result<?> logout() {
        return Result.success("退出成功");
    }

    @PostMapping("/password")
    public Result<?> updatePassword(@RequestBody Map<String, String> params) {
        User user = currentUser();
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        boolean success = userService.updatePassword(user.getId(), params.get("oldPassword"), params.get("newPassword"));
        return success ? Result.success("密码修改成功") : Result.error("原密码错误");
    }

    @GetMapping("/list")
    public Result<?> getUserList() {
        User admin = currentUser();
        if (!isAdmin(admin)) {
            return Result.error(403, "无权限访问");
        }
        List<User> list = userService.getAllUsers();
        return Result.success(list);
    }

    @PutMapping("/{id}")
    public Result<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        User admin = currentUser();
        if (!isAdmin(admin)) {
            return Result.error(403, "无权限访问");
        }
        user.setId(id);
        boolean success = userService.updateUser(user);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    @PostMapping("/{id}/status")
    public Result<?> toggleStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        User admin = currentUser();
        if (!isAdmin(admin)) {
            return Result.error(403, "无权限访问");
        }
        boolean success = userService.toggleStatus(id, params.get("status"));
        return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }

    @PostMapping("/{id}/credit")
    public Result<?> updateCreditScore(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        User admin = currentUser();
        if (!isAdmin(admin)) {
            return Result.error(403, "无权限访问");
        }
        boolean success = userService.updateCreditScore(id, params.get("score"));
        return success ? Result.success("信用分更新成功") : Result.error("信用分更新失败");
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
