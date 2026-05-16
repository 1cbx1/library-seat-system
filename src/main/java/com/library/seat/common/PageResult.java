package com.library.seat.common;

import lombok.Data;
import java.util.List;

/**
 * 分页结果
 */
@Data
public class PageResult<T> {
    
    private Long total;
    private List<T> records;
    private Long current;
    private Long size;
    
    public static <T> PageResult<T> ok(Long total, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setRecords(records);
        return result;
    }
}
