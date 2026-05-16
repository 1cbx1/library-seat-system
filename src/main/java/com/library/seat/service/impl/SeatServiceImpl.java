package com.library.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.seat.entity.Seat;
import com.library.seat.mapper.SeatMapper;
import com.library.seat.service.SeatService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatServiceImpl extends ServiceImpl<SeatMapper, Seat> implements SeatService {

    private static final Integer AVAILABLE = 1;
    private static final Integer OCCUPIED = 2;
    private static final Integer RESERVED = 3;
    private static final Integer LOCKED = 4;

    @Override
    public List<Seat> getByRoomId(Long roomId) {
        QueryWrapper<Seat> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", roomId);
        wrapper.orderByAsc("seat_code");
        return this.list(wrapper);
    }

    @Override
    public List<Seat> getAllSeats() {
        QueryWrapper<Seat> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("room_id", "seat_code");
        return this.list(wrapper);
    }

    @Override
    public boolean addSeat(Seat seat) {
        if (seat.getSeatCode() == null || seat.getSeatCode().trim().isEmpty()) {
            seat.setSeatCode("S" + System.currentTimeMillis());
        }
        if (seat.getStatus() == null) {
            seat.setStatus(AVAILABLE);
        }
        return this.save(seat);
    }

    @Override
    public boolean updateSeat(Seat seat) {
        return this.updateById(seat);
    }

    @Override
    public boolean deleteSeat(Long id) {
        return this.removeById(id);
    }

    @Override
    public boolean lockSeat(Long seatId) {
        Seat seat = this.getById(seatId);
        if (seat == null || !AVAILABLE.equals(seat.getStatus())) {
            return false;
        }
        seat.setStatus(RESERVED);
        return this.updateById(seat);
    }

    @Override
    public boolean occupySeat(Long seatId) {
        Seat seat = this.getById(seatId);
        if (seat == null || !(AVAILABLE.equals(seat.getStatus()) || RESERVED.equals(seat.getStatus()))) {
            return false;
        }
        seat.setStatus(OCCUPIED);
        return this.updateById(seat);
    }

    @Override
    public boolean unlockSeat(Long seatId) {
        Seat seat = this.getById(seatId);
        if (seat == null) {
            return false;
        }
        if (RESERVED.equals(seat.getStatus()) || OCCUPIED.equals(seat.getStatus())) {
            seat.setStatus(AVAILABLE);
            return this.updateById(seat);
        }
        return !LOCKED.equals(seat.getStatus());
    }
}
