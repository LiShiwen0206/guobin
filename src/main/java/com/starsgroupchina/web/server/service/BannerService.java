package com.starsgroupchina.web.server.service;

import com.starsgroupchina.common.base.AbstractService;
import com.starsgroupchina.common.objects.If;
import com.starsgroupchina.common.utils.DateUtil;
import com.starsgroupchina.web.server.bean.mapper.BannerMapper;
import com.starsgroupchina.web.server.bean.model.Banner;
import com.starsgroupchina.web.server.bean.model.BannerExample;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class BannerService extends AbstractService<Banner, BannerExample> {

    @Autowired
    private BannerMapper bannerMapper;

    public void updateBanners(List<Banner> banners) {
        //删除现有所有数据
        bannerMapper.deleteByExample(null);
        super.create(banners);
    }

}
