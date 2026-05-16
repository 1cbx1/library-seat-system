package com.library.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.seat.entity.ForumComment;
import com.library.seat.entity.User;
import com.library.seat.mapper.ForumCommentMapper;
import com.library.seat.mapper.UserMapper;
import com.library.seat.service.ForumCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 论坛评论 Service 实现
 */
@Service
public class ForumCommentServiceImpl extends ServiceImpl<ForumCommentMapper, ForumComment> implements ForumCommentService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<ForumComment> getByPostId(Long postId) {
        QueryWrapper<ForumComment> wrapper = new QueryWrapper<>();
        wrapper.eq("post_id", postId);
        wrapper.orderByAsc("create_time");
        List<ForumComment> comments = this.list(wrapper);

        // 填充用户信息
        for (ForumComment comment : comments) {
            if (comment.getUserId() != null) {
                User user = userMapper.selectById(comment.getUserId());
                if (user != null) {
                    comment.setUserName(user.getRealName() != null ? user.getRealName() : user.getUsername());
                    comment.setUserAvatar(user.getAvatar());
                }
            }
            // 填充被回复人信息
            if (comment.getParentId() != null) {
                ForumComment parentComment = this.getById(comment.getParentId());
                if (parentComment != null) {
                    User parentUser = userMapper.selectById(parentComment.getUserId());
                    if (parentUser != null) {
                        String replyToName = parentUser.getRealName() != null ? parentUser.getRealName() : parentUser.getUsername();
                        // 使用反射设置replyToName属性，或者添加一个字段
                        comment.setUserName(comment.getUserName() + " 回复");
                        // 这里可以扩展添加replyToName字段
                    }
                }
            }
        }
        return comments;
    }

    @Override
    public ForumComment addComment(ForumComment comment) {
        this.save(comment);
        return comment;
    }

    @Override
    public boolean deleteComment(Long id) {
        return this.removeById(id);
    }

    @Override
    public Long getCountByPostId(Long postId) {
        QueryWrapper<ForumComment> wrapper = new QueryWrapper<>();
        wrapper.eq("post_id", postId);
        return this.count(wrapper);
    }
}
