package com.library.seat.controller;

import com.library.seat.common.Result;
import com.library.seat.entity.Evaluation;
import com.library.seat.entity.Reservation;
import com.library.seat.entity.User;
import com.library.seat.service.EvaluationService;
import com.library.seat.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluation")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/room/{roomId}")
    public Result<?> getByRoom(@PathVariable Long roomId) {
        List<Evaluation> list = evaluationService.getByRoomId(roomId);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        result.put("avgScore", evaluationService.getAverageScore(roomId));
        result.put("stats", evaluationService.getScoreStats(roomId));
        return Result.success(result);
    }

    @GetMapping("/reservation/{reservationId}")
    public Result<?> getByReservation(@PathVariable Long reservationId) {
        return Result.success(evaluationService.getByReservationId(reservationId));
    }

    @GetMapping("/pending/{userId}")
    public Result<?> getPendingEvaluations(@PathVariable Long userId) {
        return Result.success(reservationService.getUserReservations(userId));
    }

    @PostMapping
    public Result<?> addEvaluation(@RequestBody Evaluation evaluation) {
        User user = currentUser();
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        if (evaluation.getReservationId() == null) {
            return Result.error("缺少预约ID");
        }
        Reservation reservation = reservationService.getById(evaluation.getReservationId());
        if (reservation == null || !reservation.getUserId().equals(user.getId())) {
            return Result.error(403, "无权评价该预约");
        }
        if (!"completed".equals(reservation.getStatus())) {
            return Result.error("预约完成后才能评价");
        }
        if (evaluationService.getByReservationId(evaluation.getReservationId()) != null) {
            return Result.error("该预约已评价");
        }
        evaluation.setUserId(user.getId());
        evaluation.setRoomId(reservation.getRoomId());
        return Result.success("评价成功", evaluationService.addEvaluation(evaluation));
    }

    @GetMapping("/stats/{roomId}")
    public Result<?> getStats(@PathVariable Long roomId) {
        Map<String, Object> result = new HashMap<>();
        result.put("avgScore", evaluationService.getAverageScore(roomId));
        result.put("stats", evaluationService.getScoreStats(roomId));
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<?> getAll(@RequestParam(required = false) Long roomId) {
        User user = currentUser();
        if (user == null || !"admin".equals(user.getRole())) {
            return Result.error(403, "无权限访问");
        }
        return Result.success(evaluationService.getAll(roomId));
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteEvaluation(@PathVariable Long id) {
        User user = currentUser();
        if (user == null || !"admin".equals(user.getRole())) {
            return Result.error(403, "无权限访问");
        }
        boolean success = evaluationService.deleteEvaluation(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return null;
        }
        return (User) authentication.getPrincipal();
    }
}
