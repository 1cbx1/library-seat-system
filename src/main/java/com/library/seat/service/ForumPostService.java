package com.library.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.seat.entity.ForumPost;
import java.util.List;

/**
 * 论坛帖子 Service 接口
 */
public interface ForumPostService extends IService<ForumPost> {
    
    /**
     * 获取活跃帖子列表
     */
    List<ForumPost> getActiveList();
    
    /**
     * 获取帖子详情
     */
    ForumPost getDetail(Long id);
    
    /**
     * 发布帖子
     */
    ForumPost publish(ForumPost post, Long userId);
    
    /**
     * 点赞
     */
    boolean like(Long id);
    
    /**
     * 收藏
     */
    boolean collect(Long postId, Long userId);
}
