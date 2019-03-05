package com.starsgroupchina.web.server.controller.management;

import com.starsgroupchina.common.objects.If;
import com.starsgroupchina.common.response.ListResponse;
import com.starsgroupchina.common.response.SimpleResponse;
import com.starsgroupchina.common.utils.DateUtil;
import com.starsgroupchina.web.server.ErrorCode;
import com.starsgroupchina.web.server.KeyCache;
import com.starsgroupchina.web.server.bean.model.Banner;
import com.starsgroupchina.web.server.bean.model.BannerExample;
import com.starsgroupchina.web.server.service.BannerService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * bannerController
 *
 */
@Slf4j
@Api
@RestController
@ApiResponses({@ApiResponse(code = ErrorCode.AUTH_ERROR, message = "获取用户信息失败，请登录后重试；返回登陆")})
@RequestMapping(value = "/management/banner", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @ApiOperation("查询列表")
    @GetMapping
    public ListResponse<Banner> query(@RequestParam(value = "index", defaultValue = "1") int index,
                                      @RequestParam(value = "limit", defaultValue = "20") int limit,
                                      @RequestParam(value = "status", required = false) List<Short> status,
                                      @RequestParam(value = "begin", defaultValue = "1451630690") Long begin,
                                      @RequestParam(value = "end", defaultValue = "1895538782000") Long end) {
        BannerExample example = new BannerExample();
        BannerExample.Criteria criteria = example.createCriteria();
        If.of(!CollectionUtils.isEmpty(status)).isTrue(() -> criteria.andStatusIn(status));
        example.setAdditionalWhere(String.format("(create_time BETWEEN '%s' AND '%s')", DateUtil.timestampReversDate(begin), DateUtil.timestampReversDate(end)));
        long count = bannerService.count(example);
        example.setOrderByClause("create_time desc");
        example.setOffset((index - 1) * limit);
        example.setLimit(limit);
        List<Banner> result = bannerService.query(example).collect(toList());
        return ListResponse.success(result, count, index, limit);
    }

    @ApiOperation("根据id获取")
    @GetMapping("/{id}")
    public SimpleResponse<Banner> get(@PathVariable("id") Integer id) {
        //根据id获取
        Banner result = bannerService.getById(id);
        return SimpleResponse.success(result);
    }

    @ApiOperation("新增，修改Banner")
    @PutMapping
    @CacheEvict(value = KeyCache.CACHE_BANNER, allEntries=true)
    public SimpleResponse update(@ApiParam("首页轮播图集合（用，分隔）")@RequestBody List<Banner> banners) {
        bannerService.updateBanners(banners);
        return SimpleResponse.success();
    }

    @ApiOperation("删除Banner")
    @DeleteMapping("/{id}")
    public SimpleResponse delete(@PathVariable("id") Integer id) {
        bannerService.deleteById(id);
        return SimpleResponse.success();
    }

}
