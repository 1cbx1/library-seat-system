package com.library.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.seat.entity.User;
import java.util.List;

/**
 * 用户 Service 接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户登录
     */
    User login(String username, String password);
    
    /**
     * 根据用户名查询
     */
    User findByUsername(String username);
    
    /**
     * 用户注册
     */
    boolean register(User user);
    
    /**
     * 修改密码
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 获取所有用户列表
     */
    List<User> getAllUsers();
    
    /**
     * 更新用户信息
     */
    boolean updateUser(User user);
    
    /**
     * 禁用/启用用户
     */
    boolean toggleStatus(Long userId, Integer status);
    
    /**
     * 更新用户信用分
     */
    boolean updateCreditScore(Long userId, Integer score);
}
