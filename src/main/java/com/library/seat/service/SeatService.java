package com.library.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.seat.entity.Seat;

import java.util.List;

public interface SeatService extends IService<Seat> {

    List<Seat> getByRoomId(Long roomId);

    List<Seat> getAllSeats();

    boolean addSeat(Seat seat);

    boolean updateSeat(Seat seat);

    boolean deleteSeat(Long id);

    boolean lockSeat(Long seatId);

    boolean occupySeat(Long seatId);

    boolean unlockSeat(Long seatId);
}
