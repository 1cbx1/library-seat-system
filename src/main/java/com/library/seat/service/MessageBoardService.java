package com.library.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.seat.entity.MessageBoard;
import java.util.List;

/**
 * 留言板 Service 接口
 */
public interface MessageBoardService extends IService<MessageBoard> {
    
    /**
     * 获取留言列表
     */
    List<MessageBoard> getList();
    
    /**
     * 用户提交留言
     */
    boolean submit(MessageBoard message, Long userId);
    
    /**
     * 回复留言
     */
    boolean reply(Long id, String replyContent, Long adminId);
}
