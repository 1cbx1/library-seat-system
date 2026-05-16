package com.library.seat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.seat.entity.Banner;
import java.util.List;

/**
 * 轮播图 Service 接口
 */
public interface BannerService extends IService<Banner> {
    
    /**
     * 获取启用的轮播图列表
     */
    List<Banner> getActiveList();
    
    /**
     * 添加轮播图
     */
    boolean addBanner(Banner banner);
}
