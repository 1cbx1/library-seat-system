package com.library.seat.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.library.seat.entity.Reservation;
import com.library.seat.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 违约预测提醒任务
 * 每隔1分钟检查是否有即将到期的预约
 */
@Component
public class ReminderTask {

    private static final Logger logger = LoggerFactory.getLogger(ReminderTask.class);

    // 提前提醒时间（分钟）
    private static final int REMINDER_MINUTES = 15;

    @Autowired
    private ReservationService reservationService;

    /**
     * 每隔1分钟检查即将到期的预约
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void checkUpcomingReservations() {
        QueryWrapper<Reservation> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "checked_in");
        // 结束时间在接下来的15分钟内
        wrapper.ge("end_time", LocalDateTime.now());
        wrapper.le("end_time", LocalDateTime.now().plusMinutes(REMINDER_MINUTES));

        List<Reservation> upcomingList = reservationService.list(wrapper);

        for (Reservation reservation : upcomingList) {
            logger.info("提醒：预约 {} 将在15分钟内到期", reservation.getReservationNo());
            // 这里可以添加WebSocket推送或其他通知方式
        }

        if (!upcomingList.isEmpty()) {
            logger.info("发现{}个即将到期的预约", upcomingList.size());
        }
    }
}