package com.library.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.seat.entity.ForumComment;
import java.util.List;

/**
 * 论坛评论 Service 接口
 */
public interface ForumCommentService extends IService<ForumComment> {
    
    /**
     * 根据帖子ID获取评论列表
     */
    List<ForumComment> getByPostId(Long postId);
    
    /**
     * 添加评论
     */
    ForumComment addComment(ForumComment comment);
    
    /**
     * 删除评论
     */
    boolean deleteComment(Long id);
    
    /**
     * 获取帖子的评论数
     */
    Long getCountByPostId(Long postId);
}
