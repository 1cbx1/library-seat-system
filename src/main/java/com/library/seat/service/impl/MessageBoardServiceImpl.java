package com.library.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.seat.entity.MessageBoard;
import com.library.seat.mapper.MessageBoardMapper;
import com.library.seat.service.MessageBoardService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 留言板 Service 实现
 */
@Service
public class MessageBoardServiceImpl extends ServiceImpl<MessageBoardMapper, MessageBoard> implements MessageBoardService {

    @Override
    public List<MessageBoard> getList() {
        QueryWrapper<MessageBoard> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return this.list(wrapper);
    }

    @Override
    public boolean submit(MessageBoard message, Long userId) {
        message.setUserId(userId);
        message.setStatus(0); // 待回复
        return this.save(message);
    }

    @Override
    public boolean reply(Long id, String replyContent, Long adminId) {
        MessageBoard message = this.getById(id);
        if (message == null) return false;
        message.setReplyContent(replyContent);
        message.setReplyId(adminId);
        message.setStatus(1); // 已回复
        return this.updateById(message);
    }
}
