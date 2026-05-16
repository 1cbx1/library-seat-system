package com.library.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.seat.entity.SensitiveWord;

/**
 * 敏感词 Service 接口
 */
public interface SensitiveWordService extends IService<SensitiveWord> {
    
    /**
     * 添加敏感词
     */
    boolean addWord(SensitiveWord word);
    
    /**
     * 检查内容是否包含敏感词
     */
    boolean containsSensitive(String content);
}
