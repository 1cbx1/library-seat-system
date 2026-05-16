package com.library.seat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.seat.entity.CheckIn;
import org.apache.ibatis.annotations.Mapper;

/**
 * 签到 Mapper
 */
@Mapper
public interface CheckInMapper extends BaseMapper<CheckIn> {
}
