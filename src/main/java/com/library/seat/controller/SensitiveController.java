package com.library.seat.controller;

import com.library.seat.common.Result;
import com.library.seat.entity.SensitiveWord;
import com.library.seat.service.SensitiveWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 敏感词 Controller
 */
@RestController
@RequestMapping("/api/sensitive")
public class SensitiveController {

    @Autowired
    private SensitiveWordService sensitiveWordService;

    /**
     * 获取敏感词列表（后台）
     */
    @GetMapping("/list")
    public Result<?> getList() {
        List<SensitiveWord> list = sensitiveWordService.list();
        return Result.success(list);
    }

    /**
     * 添加敏感词（后台）
     */
    @PostMapping
    public Result<?> add(@RequestBody SensitiveWord word) {
        boolean success = sensitiveWordService.addWord(word);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 删除敏感词（后台）
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        boolean success = sensitiveWordService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 检查内容是否包含敏感词
     */
    @PostMapping("/check")
    public Result<?> check(@RequestBody java.util.Map<String, String> params) {
        String content = params.get("content");
        boolean hasSensitive = sensitiveWordService.containsSensitive(content);
        return Result.success(hasSensitive);
    }
}
