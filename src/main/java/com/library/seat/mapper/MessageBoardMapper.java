package com.library.seat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.seat.entity.MessageBoard;
import org.apache.ibatis.annotations.Mapper;

/**
 * 留言板 Mapper
 */
@Mapper
public interface MessageBoardMapper extends BaseMapper<MessageBoard> {
}
