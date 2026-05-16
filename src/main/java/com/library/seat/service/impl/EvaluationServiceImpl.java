package com.library.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.seat.entity.Evaluation;
import com.library.seat.entity.User;
import com.library.seat.mapper.EvaluationMapper;
import com.library.seat.mapper.UserMapper;
import com.library.seat.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评价 Service 实现
 */
@Service
public class EvaluationServiceImpl extends ServiceImpl<EvaluationMapper, Evaluation> implements EvaluationService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Evaluation> getByRoomId(Long roomId) {
        QueryWrapper<Evaluation> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", roomId);
        wrapper.orderByDesc("create_time");
        List<Evaluation> list = this.list(wrapper);
        
        // 填充用户信息
        for (Evaluation eval : list) {
            if (eval.getUserId() != null) {
                User user = userMapper.selectById(eval.getUserId());
                if (user != null) {
                    eval.setUserName(user.getRealName() != null ? user.getRealName() : user.getUsername());
                }
            }
        }
        return list;
    }

    @Override
    public Evaluation getByReservationId(Long reservationId) {
        QueryWrapper<Evaluation> wrapper = new QueryWrapper<>();
        wrapper.eq("reservation_id", reservationId);
        return this.getOne(wrapper);
    }

    @Override
    public Evaluation addEvaluation(Evaluation evaluation) {
        this.save(evaluation);
        return evaluation;
    }

    @Override
    public Double getAverageScore(Long roomId) {
        QueryWrapper<Evaluation> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", roomId);
        List<Evaluation> list = this.list(wrapper);
        
        if (list.isEmpty()) {
            return 0.0;
        }
        
        double sum = 0;
        for (Evaluation eval : list) {
            sum += eval.getScore();
        }
        return Math.round(sum / list.size() * 10) / 10.0;
    }

    @Override
    public Map<Integer, Long> getScoreStats(Long roomId) {
        QueryWrapper<Evaluation> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", roomId);
        List<Evaluation> list = this.list(wrapper);

        Map<Integer, Long> stats = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            stats.put(i, 0L);
        }

        for (Evaluation eval : list) {
            int score = eval.getScore();
            stats.put(score, stats.getOrDefault(score, 0L) + 1);
        }

        return stats;
    }

    @Override
    public List<Evaluation> getAll(Long roomId) {
        QueryWrapper<Evaluation> wrapper = new QueryWrapper<>();
        if (roomId != null) {
            wrapper.eq("room_id", roomId);
        }
        wrapper.orderByDesc("create_time");
        List<Evaluation> list = this.list(wrapper);

        // 填充用户和自习室信息
        for (Evaluation eval : list) {
            if (eval.getUserId() != null) {
                User user = userMapper.selectById(eval.getUserId());
                if (user != null) {
                    eval.setUserName(user.getRealName() != null ? user.getRealName() : user.getUsername());
                }
            }
        }
        return list;
    }

    @Override
    public boolean deleteEvaluation(Long id) {
        return this.removeById(id);
    }
}
