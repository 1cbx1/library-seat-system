package com.library.seat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.seat.entity.SensitiveWord;
import com.library.seat.mapper.SensitiveWordMapper;
import com.library.seat.service.SensitiveWordService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 敏感词 Service 实现
 */
@Service
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWord> implements SensitiveWordService {

    @Override
    public boolean addWord(SensitiveWord word) {
        word.setStatus(1);
        return this.save(word);
    }

    @Override
    public boolean containsSensitive(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        List<SensitiveWord> words = this.list();
        for (SensitiveWord word : words) {
            if ("content".equals(word.getType()) && word.getStatus() == 1) {
                if (content.contains(word.getWord())) {
                    return true;
                }
            }
        }
        return false;
    }
}
