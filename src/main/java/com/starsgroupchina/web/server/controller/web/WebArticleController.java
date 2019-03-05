package com.starsgroupchina.web.server.controller.web;

import com.starsgroupchina.common.objects.If;
import com.starsgroupchina.common.response.ListResponse;
import com.starsgroupchina.common.response.SimpleResponse;
import com.starsgroupchina.web.server.KeyCache;
import com.starsgroupchina.web.server.bean.model.Article;
import com.starsgroupchina.web.server.bean.model.ArticleExample;
import com.starsgroupchina.web.server.service.ArticleService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 *webArticleController
 */
@Slf4j
@Api
@RestController
@RequestMapping(value = "/article", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WebArticleController {

    @Autowired
    private ArticleService articleService;

    @ApiOperation("查询列表")
    @GetMapping
    public ListResponse<Article> query(@RequestParam(value = "index", defaultValue = "1") int index,
                                       @RequestParam(value = "limit", defaultValue = "20") int limit,
                                       @RequestParam(value = "status", required = false) List<Short> status,
                                       @ApiParam("发布状态，默认为1发布状态") @RequestParam(value = "publishState", defaultValue = "1") Short publishState,
                                       @ApiParam("文章分类集合（用，分隔各个分类；eg：企业介绍，投资策略）") @RequestParam(value = "categoryList", required = false) List<String> categoryList) {
        ArticleExample example = new ArticleExample();
        ArticleExample.Criteria criteria = example.createCriteria();
        If.of(!CollectionUtils.isEmpty(status)).isTrue(() -> criteria.andStatusIn(status));
        criteria.andPublishStateEqualTo(publishState);
        If.of(CollectionUtils.isNotEmpty(categoryList)).isTrue(() -> criteria.andCategoryIn(categoryList));
        long count = articleService.count(example);
        example.setOrderByClause("stick_time desc, create_time desc");
        example.setOffset((index - 1) * limit);
        example.setLimit(limit);
        List<Article> result = articleService.query(example).collect(toList());
        return ListResponse.success(result, count, index, limit);
    }

    @ApiOperation("根据id获取")
    @GetMapping("/{id}")
    @Cacheable(value = KeyCache.CACHE_ARTICLE, key = "'article_id:'+#id")
    public SimpleResponse<Article> get(@PathVariable("id") Integer id) {
        //根据id获取
        Article result = articleService.getById(id);
        return SimpleResponse.success(result);
    }

}
