package com.library.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.seat.entity.Announcement;
import com.library.seat.mapper.AnnouncementMapper;
import com.library.seat.service.AnnouncementService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 公告 Service 实现
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Override
    public List<Announcement> getActiveList() {
        QueryWrapper<Announcement> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByDesc("top").orderByDesc("create_time");
        return this.list(wrapper);
    }

    @Override
    public boolean publish(Announcement announcement) {
        announcement.setStatus(1);
        return this.save(announcement);
    }
    
    @Override
    public boolean setTop(Long id, Integer top) {
        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setTop(top);
        return this.updateById(announcement);
    }
}
