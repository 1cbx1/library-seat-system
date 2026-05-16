package com.library.seat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.seat.entity.Banner;
import com.library.seat.mapper.BannerMapper;
import com.library.seat.service.BannerService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 轮播图 Service 实现
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public List<Banner> getActiveList() {
        QueryWrapper<Banner> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByAsc("sort");
        return this.list(wrapper);
    }

    @Override
    public boolean addBanner(Banner banner) {
        banner.setStatus(1);
        return this.save(banner);
    }
}
