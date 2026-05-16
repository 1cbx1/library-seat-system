package com.library.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.seat.entity.Evaluation;
import java.util.List;
import java.util.Map;

/**
 * 评价 Service 接口
 */
public interface EvaluationService extends IService<Evaluation> {

    /**
     * 根据自习室获取评价列表
     */
    List<Evaluation> getByRoomId(Long roomId);

    /**
     * 根据预约ID获取评价
     */
    Evaluation getByReservationId(Long reservationId);

    /**
     * 添加评价
     */
    Evaluation addEvaluation(Evaluation evaluation);

    /**
     * 获取自习室的平均评分
     */
    Double getAverageScore(Long roomId);

    /**
     * 获取自习室评分统计
     */
    Map<Integer, Long> getScoreStats(Long roomId);

    /**
     * 获取所有评价（后台）
     */
    List<Evaluation> getAll(Long roomId);

    /**
     * 删除评价
     */
    boolean deleteEvaluation(Long id);
}
