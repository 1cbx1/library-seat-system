package com.library.seat.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录限制服务
 * 防止暴力破解密码
 */
@Component
public class LoginLimitService {

    private static final Logger logger = LoggerFactory.getLogger(LoginLimitService.class);

    // 最大登录失败次数
    private static final int MAX_FAIL_COUNT = 5;

    // 锁定时间（分钟）
    private static final int LOCK_MINUTES = 15;

    // 登录失败记录: username -> (失败次数, 锁定截止时间)
    private final Map<String, LoginFailRecord> failRecords = new ConcurrentHashMap<>();

    /**
     * 检查是否被锁定
     */
    public boolean isLocked(String username) {
        LoginFailRecord record = failRecords.get(username);
        if (record == null) {
            return false;
        }
        // 检查是否已超过锁定时间
        if (record.getLockUntil() != null && record.getLockUntil().isBefore(java.time.LocalDateTime.now())) {
            failRecords.remove(username);
            return false;
        }
        return record.getLockUntil() != null;
    }

    /**
     * 记录登录失败
     */
    public void recordFail(String username) {
        LoginFailRecord record = failRecords.get(username);
        if (record == null) {
            record = new LoginFailRecord();
            failRecords.put(username, record);
        }
        record.incrementFailCount();

        // 如果失败次数达到阈值，锁定账户
        if (record.getFailCount() >= MAX_FAIL_COUNT) {
            record.setLockUntil(java.time.LocalDateTime.now().plusMinutes(LOCK_MINUTES));
            logger.warn("用户{}登录失败{}次，锁定{}分钟", username, MAX_FAIL_COUNT, LOCK_MINUTES);
        }
    }

    /**
     * 登录成功，清除失败记录
     */
    public void recordSuccess(String username) {
        failRecords.remove(username);
    }

    /**
     * 获取剩余可尝试次数
     */
    public int getRemainingAttempts(String username) {
        LoginFailRecord record = failRecords.get(username);
        if (record == null) {
            return MAX_FAIL_COUNT;
        }
        return Math.max(0, MAX_FAIL_COUNT - record.getFailCount());
    }

    /**
     * 获取锁定剩余时间（分钟）
     */
    public long getLockRemainingMinutes(String username) {
        LoginFailRecord record = failRecords.get(username);
        if (record == null || record.getLockUntil() == null) {
            return 0;
        }
        java.time.Duration duration = java.time.Duration.between(java.time.LocalDateTime.now(), record.getLockUntil());
        return Math.max(0, duration.toMinutes());
    }

    /**
     * 登录失败记录
     */
    private static class LoginFailRecord {
        private int failCount;
        private java.time.LocalDateTime lockUntil;

        public int getFailCount() {
            return failCount;
        }

        public void incrementFailCount() {
            this.failCount++;
        }

        public java.time.LocalDateTime getLockUntil() {
            return lockUntil;
        }

        public void setLockUntil(java.time.LocalDateTime lockUntil) {
            this.lockUntil = lockUntil;
        }
    }
}