package com.library.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.seat.entity.User;
import com.library.seat.mapper.UserMapper;
import com.library.seat.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 用户 Service 实现
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    // BCrypt密码加密器
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User login(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            logger.warn("登录失败: 用户不存在 - {}", username);
            return null;
        }
        // 使用BCrypt密码比对
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("登录失败: 密码错误 - {}", username);
            return null;
        }
        logger.info("用户登录成功: username={}, role={}", username, user.getRole());
        return user;
    }

    @Override
    public User findByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return this.getOne(wrapper);
    }

    @Override
    public boolean register(User user) {
        // 检查用户名是否存在
        if (findByUsername(user.getUsername()) != null) {
            return false;
        }
        // 使用BCrypt加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("user");
        user.setCreditScore(100);
        user.setStatus(1);
        return this.save(user);
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = this.getById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return this.updateById(user);
    }

    @Override
    public List<User> getAllUsers() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return this.list(wrapper);
    }

    @Override
    public boolean updateUser(User user) {
        return this.updateById(user);
    }

    @Override
    public boolean toggleStatus(Long userId, Integer status) {
        User user = this.getById(userId);
        if (user == null) return false;
        user.setStatus(status);
        return this.updateById(user);
    }

    @Override
    public boolean updateCreditScore(Long userId, Integer score) {
        User user = this.getById(userId);
        if (user == null) return false;
        user.setCreditScore(score);
        return this.updateById(user);
    }
}
