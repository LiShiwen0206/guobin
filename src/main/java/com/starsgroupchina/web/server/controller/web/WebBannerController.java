package com.starsgroupchina.web.server.controller.web;

import com.starsgroupchina.common.objects.If;
import com.starsgroupchina.common.response.ListResponse;
import com.starsgroupchina.common.response.SimpleResponse;
import com.starsgroupchina.web.server.KeyCache;
import com.starsgroupchina.web.server.bean.model.Banner;
import com.starsgroupchina.web.server.bean.model.BannerExample;
import com.starsgroupchina.web.server.service.BannerService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * webBannerController
 *
 */
@Slf4j
@Api
@RestController
@RequestMapping(value = "/banner", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WebBannerController {

    @Autowired
    private BannerService bannerService;

    @ApiOperation("查询列表")
    @GetMapping
    public ListResponse<Banner> query(@RequestParam(value = "index", defaultValue = "1") int index,
                                      @RequestParam(value = "limit", defaultValue = "20") int limit,
                                      @RequestParam(value = "status", required = false) List<Short> status) {
        BannerExample example = new BannerExample();
        BannerExample.Criteria criteria = example.createCriteria();
        If.of(!CollectionUtils.isEmpty(status)).isTrue(() -> criteria.andStatusIn(status));
        long count = bannerService.count(example);
        example.setOrderByClause("create_time desc");
        example.setOffset((index - 1) * limit);
        example.setLimit(limit);
        List<Banner> result = bannerService.query(example).collect(toList());
        return ListResponse.success(result, count, index, limit);
    }

    @ApiOperation("根据id获取")
    @GetMapping("/{id}")
    @Cacheable(value = KeyCache.CACHE_BANNER, key = "'banner_id:'+#id")
    public SimpleResponse<Banner> get(@PathVariable("id") Integer id) {
        //根据id获取
        Banner result = bannerService.getById(id);
        return SimpleResponse.success(result);
    }

}
