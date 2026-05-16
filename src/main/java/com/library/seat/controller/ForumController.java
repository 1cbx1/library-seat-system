package com.library.seat.controller;

import com.library.seat.common.Result;
import com.library.seat.entity.ForumComment;
import com.library.seat.entity.ForumPost;
import com.library.seat.entity.User;
import com.library.seat.service.ForumCommentService;
import com.library.seat.service.ForumPostService;
import com.library.seat.service.SensitiveWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
public class ForumController {

    @Autowired
    private ForumPostService forumPostService;

    @Autowired
    private ForumCommentService forumCommentService;

    @Autowired
    private SensitiveWordService sensitiveWordService;

    @GetMapping("/list")
    public Result<?> getList() {
        List<ForumPost> list = forumPostService.getActiveList();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<?> getDetail(@PathVariable Long id) {
        ForumPost post = forumPostService.getDetail(id);
        if (post == null) {
            return Result.error("帖子不存在");
        }
        List<ForumComment> comments = forumCommentService.getByPostId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("post", post);
        result.put("comments", comments);
        result.put("commentCount", comments.size());
        return Result.success(result);
    }

    @PostMapping
    public Result<?> publish(@RequestBody ForumPost post) {
        User user = currentUser();
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        if (sensitiveWordService.containsSensitive(post.getContent())) {
            return Result.error("内容包含敏感词，请修改后再发布");
        }
        ForumPost result = forumPostService.publish(post, user.getId());
        return Result.success("发布成功", result);
    }

    @PostMapping("/like/{id}")
    public Result<?> like(@PathVariable Long id) {
        boolean success = forumPostService.like(id);
        return success ? Result.success("点赞成功") : Result.error("点赞失败");
    }

    @PostMapping("/collect/{id}")
    public Result<?> collect(@PathVariable Long id) {
        User user = currentUser();
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        boolean success = forumPostService.collect(id, user.getId());
        return success ? Result.success("收藏成功") : Result.error("收藏失败");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        boolean success = forumPostService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PostMapping("/comment")
    public Result<?> addComment(@RequestBody ForumComment comment) {
        User user = currentUser();
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        if (sensitiveWordService.containsSensitive(comment.getContent())) {
            return Result.error("评论包含敏感词，请修改后再发布");
        }
        comment.setUserId(user.getId());
        ForumComment result = forumCommentService.addComment(comment);
        ForumPost post = forumPostService.getById(comment.getPostId());
        if (post != null) {
            post.setCommentCount(post.getCommentCount() == null ? 1 : post.getCommentCount() + 1);
            forumPostService.updateById(post);
        }
        return Result.success("评论成功", result);
    }

    @DeleteMapping("/comment/{id}")
    public Result<?> deleteComment(@PathVariable Long id) {
        if (!isAdmin()) {
            return Result.error(403, "无权限访问");
        }
        ForumComment comment = forumCommentService.getById(id);
        if (comment != null) {
            ForumPost post = forumPostService.getById(comment.getPostId());
            if (post != null && post.getCommentCount() != null && post.getCommentCount() > 0) {
                post.setCommentCount(post.getCommentCount() - 1);
                forumPostService.updateById(post);
            }
        }
        boolean success = forumCommentService.deleteComment(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @GetMapping("/{id}/comment")
    public Result<?> getComments(@PathVariable Long id) {
        List<ForumComment> comments = forumCommentService.getByPostId(id);
        return Result.success(comments);
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return null;
        }
        return (User) authentication.getPrincipal();
    }

    private boolean isAdmin() {
        User user = currentUser();
        return user != null && "admin".equals(user.getRole());
    }
}
