package com.starsgroupchina.web.server.controller.management;

import com.starsgroupchina.common.objects.If;
import com.starsgroupchina.common.response.ListResponse;
import com.starsgroupchina.common.response.SimpleResponse;
import com.starsgroupchina.common.utils.DateUtil;
import com.starsgroupchina.web.server.ErrorCode;
import com.starsgroupchina.web.server.KeyCache;
import com.starsgroupchina.web.server.bean.model.Article;
import com.starsgroupchina.web.server.bean.model.ArticleExample;
import com.starsgroupchina.web.server.service.ArticleService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 *articleController
 */
@Slf4j
@Api
@RestController
@ApiResponses({@ApiResponse(code = ErrorCode.AUTH_ERROR, message = "获取用户信息失败，请登录后重试；返回登陆")})
@RequestMapping(value = "/management/article", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @ApiOperation("查询列表")
    @GetMapping
    public ListResponse<Article> query(@RequestParam(value = "index", defaultValue = "1") int index,
                                       @RequestParam(value = "limit", defaultValue = "20") int limit,
                                       @RequestParam(value = "status", required = false) List<Short> status,
                                       @ApiParam("发布状态") @RequestParam(value = "publishState", required = false) Short publishState,
                                       @ApiParam("查询标题") @RequestParam(value = "title", required = false) String title,
                                       @ApiParam("文章分类集合（用，分隔各个分类；eg：企业介绍，投资策略）") @RequestParam(value = "categoryList", required = false) List<String> categoryList,
                                       @RequestParam(value = "begin", defaultValue = "1451630690") Long begin,
                                       @RequestParam(value = "end", defaultValue = "1895538782000") Long end) {
        ArticleExample example = new ArticleExample();
        ArticleExample.Criteria criteria = example.createCriteria();
        If.of(!CollectionUtils.isEmpty(status)).isTrue(() -> criteria.andStatusIn(status));
        If.of(!StringUtils.isEmpty(publishState)).isTrue(() -> criteria.andPublishStateEqualTo(publishState));
        If.of(!StringUtils.isEmpty(title)).isTrue(() -> criteria.andTitleLike(title));
        If.of(CollectionUtils.isNotEmpty(categoryList)).isTrue(() -> criteria.andCategoryIn(categoryList));
        example.setAdditionalWhere(String.format("(create_time BETWEEN '%s' AND '%s')", DateUtil.timestampReversDate(begin), DateUtil.timestampReversDate(end)));
        long count = articleService.count(example);
        example.setOrderByClause("stick_time desc, create_time desc");
        example.setOffset((index - 1) * limit);
        example.setLimit(limit);
        List<Article> result = articleService.query(example).collect(toList());
        return ListResponse.success(result, count, index, limit);
    }

    @ApiOperation("根据id获取")
    @GetMapping("/{id}")
    public SimpleResponse<Article> get(@PathVariable("id") Integer id) {
        //根据id获取
        Article result = articleService.getById(id);
        return SimpleResponse.success(result);
    }

    @ApiOperation("新增Article")
    @PostMapping
    public SimpleResponse<Article> create(@RequestBody Article article) {
        articleService.create(article);
        return SimpleResponse.success(article);
    }

    @ApiOperation("修改Article")
    @PutMapping
    @CacheEvict(value = KeyCache.CACHE_ARTICLE, key = "'article_id:'+#article.id")
    public SimpleResponse<Article> update(@RequestBody Article article) {
        //根据id修改
        articleService.update(article);
        return SimpleResponse.success(article);
    }

    @ApiOperation("删除Article")
    @DeleteMapping("/{id}")
    public SimpleResponse delete(@PathVariable("id") Integer id) {
        articleService.deleteById(id);
        return SimpleResponse.success();
    }

}
