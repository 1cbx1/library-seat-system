package com.library.seat.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.library.seat.entity.Reservation;
import com.library.seat.entity.User;
import com.library.seat.service.ReservationService;
import com.library.seat.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 违约检查定时任务
 * 每隔5分钟检查一次是否有预约超时未签到
 */
@Component
public class ViolationCheckTask {

    private static final Logger logger = LoggerFactory.getLogger(ViolationCheckTask.class);

    // 预约审核通过后多少分钟内必须签到（默认30分钟）
    private static final int CHECK_IN_TIMEOUT_MINUTES = 30;

    // 每次扣减的信用分
    private static final int CREDIT_DEDUCTION = 10;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    /**
     * 每隔5分钟执行一次违约检查
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void checkViolation() {
        logger.info("开始执行违约检查任务...");

        QueryWrapper<Reservation> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "approved");
        // 审核时间超过30分钟的预约
        wrapper.lt("audit_time", LocalDateTime.now().minusMinutes(CHECK_IN_TIMEOUT_MINUTES));

        List<Reservation> expiredList = reservationService.list(wrapper);

        for (Reservation reservation : expiredList) {
            try {
                // 自动取消超时预约
                reservation.setStatus("expired");
                reservation.setCancelReason("超时未签到，系统自动取消");
                reservationService.updateById(reservation);

                // 扣减信用分
                User user = userService.getById(reservation.getUserId());
                if (user != null && user.getCreditScore() != null) {
                    int newScore = Math.max(0, user.getCreditScore() - CREDIT_DEDUCTION);
                    user.setCreditScore(newScore);
                    userService.updateById(user);

                    logger.info("用户{}因超时未签到被扣除{}分，剩余信用分：{}",
                                user.getUsername(), CREDIT_DEDUCTION, newScore);
                }

                logger.info("自动处理违约预约: reservationNo={}, userId={}",
                            reservation.getReservationNo(), reservation.getUserId());

            } catch (Exception e) {
                logger.error("处理违约预约失败: reservationNo={}, error={}",
                            reservation.getReservationNo(), e.getMessage());
            }
        }

        if (!expiredList.isEmpty()) {
            logger.info("违约检查任务完成，共处理{}条违约记录", expiredList.size());
        }
    }
}
