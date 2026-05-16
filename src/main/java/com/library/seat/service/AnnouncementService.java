package com.library.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.seat.entity.Announcement;
import java.util.List;

/**
 * 公告 Service 接口
 */
public interface AnnouncementService extends IService<Announcement> {
    
    /**
     * 获取显示的公告列表
     */
    List<Announcement> getActiveList();
    
    /**
     * 发布公告
     */
    boolean publish(Announcement announcement);
    
    /**
     * 设置公告置顶或取消置顶
     */
    boolean setTop(Long id, Integer top);
}
