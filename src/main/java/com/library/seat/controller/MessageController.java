package com.library.seat.controller;

import com.library.seat.common.Result;
import com.library.seat.entity.MessageBoard;
import com.library.seat.entity.User;
import com.library.seat.service.MessageBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

/**
 * 留言板 Controller
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageBoardService messageBoardService;
    
    @Autowired
    private com.library.seat.service.SensitiveWordService sensitiveWordService;

    /**
     * 获取留言列表
     */
    @GetMapping("/list")
    public Result<?> getList() {
        List<MessageBoard> list = messageBoardService.getList();
        return Result.success(list);
    }

    /**
     * 提交留言
     */
    @PostMapping
    public Result<?> submit(@RequestBody MessageBoard message) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        if (sensitiveWordService.containsSensitive(message.getContent())) {
            return Result.error("留言内容包含敏感词，请修改后再提交");
        }
        boolean success = messageBoardService.submit(message, user.getId());
        return success ? Result.success("提交成功") : Result.error("提交失败");
    }

    /**
     * 回复留言（后台）
     */
    @PostMapping("/reply/{id}")
    public Result<?> reply(@PathVariable Long id, @RequestBody java.util.Map<String, String> params) {
        User admin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (admin == null || !"admin".equals(admin.getRole())) {
            return Result.error(401, "无权限");
        }
        String replyContent = params.get("replyContent");
        boolean success = messageBoardService.reply(id, replyContent, admin.getId());
        return success ? Result.success("回复成功") : Result.error("回复失败");
    }

    /**
     * 删除留言（后台）
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        boolean success = messageBoardService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
