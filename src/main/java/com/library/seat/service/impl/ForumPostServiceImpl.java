package com.library.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.seat.entity.ForumPost;
import com.library.seat.mapper.ForumPostMapper;
import com.library.seat.service.ForumPostService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 论坛帖子 Service 实现
 */
@Service
public class ForumPostServiceImpl extends ServiceImpl<ForumPostMapper, ForumPost> implements ForumPostService {

    @Override
    public List<ForumPost> getActiveList() {
        QueryWrapper<ForumPost> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByDesc("create_time");
        return this.list(wrapper);
    }

    @Override
    public ForumPost getDetail(Long id) {
        ForumPost post = this.getById(id);
        if (post != null) {
            // 浏览数+1
            post.setViewCount((post.getViewCount() == null ? 1 : post.getViewCount()) + 1);
            this.updateById(post);
        }
        return post;
    }

    @Override
    public ForumPost publish(ForumPost post, Long userId) {
        post.setUserId(userId);
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setCollectCount(0);
        post.setStatus(1);
        this.save(post);
        return post;
    }

    @Override
    public boolean like(Long id) {
        ForumPost post = this.getById(id);
        if (post == null) return false;
        post.setLikeCount((post.getLikeCount() == null ? 1 : post.getLikeCount()) + 1);
        return this.updateById(post);
    }

    @Override
    public boolean collect(Long postId, Long userId) {
        ForumPost post = this.getById(postId);
        if (post == null) return false;
        post.setCollectCount((post.getCollectCount() == null ? 1 : post.getCollectCount()) + 1);
        return this.updateById(post);
    }
}
