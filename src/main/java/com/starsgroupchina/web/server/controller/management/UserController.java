package com.starsgroupchina.web.server.controller.management;

import com.starsgroupchina.common.exception.AppException;
import com.starsgroupchina.common.objects.If;
import com.starsgroupchina.common.response.ListResponse;
import com.starsgroupchina.common.response.SimpleResponse;
import com.starsgroupchina.common.utils.DateUtil;
import com.starsgroupchina.common.utils.Utils;
import com.starsgroupchina.web.server.ErrorCode;
import com.starsgroupchina.web.server.bean.model.User;
import com.starsgroupchina.web.server.bean.model.UserExample;
import com.starsgroupchina.web.server.service.UserService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * userController
 */
@Slf4j
@Api
@RestController
@ApiResponses({@ApiResponse(code = ErrorCode.AUTH_ERROR, message = "获取用户信息失败，请登录后重试；返回登陆")})
@RequestMapping(value = "/management/user", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("查询列表")
    @GetMapping
    public ListResponse<User> query(@RequestParam(value = "index", defaultValue = "1") int index,
                                    @RequestParam(value = "limit", defaultValue = "20") int limit,
                                    @RequestParam(value = "status", required = false) List<Short> status,
                                    @RequestParam(value = "begin", defaultValue = "1451630690") Long begin,
                                    @RequestParam(value = "end", defaultValue = "1895538782000") Long end) {
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        If.of(!CollectionUtils.isEmpty(status)).isTrue(() -> criteria.andStatusIn(status));
        example.setAdditionalWhere(String.format("(create_time BETWEEN '%s' AND '%s')", DateUtil.timestampReversDate(begin), DateUtil.timestampReversDate(end)));
        long count = userService.count(example);
        example.setOrderByClause("create_time desc");
        example.setOffset((index - 1) * limit);
        example.setLimit(limit);
        List<User> result = userService.query(example).collect(toList());
        return ListResponse.success(result, count, index, limit);
    }

    @ApiOperation("根据id获取")
    @GetMapping("/{id}")
    public SimpleResponse<User> get(@PathVariable("id") Integer id) {
        //根据id获取
        User result = userService.getById(id);
        return SimpleResponse.success(result);
    }

    @ApiResponses({@ApiResponse(code = ErrorCode.USER_WAS_EXISTS, message = "该user已存在")})
    @ApiOperation("新增User")
    @PostMapping
    public SimpleResponse<User> create(@RequestBody User user) {
        UserExample example = new UserExample();
        example.setAdditionalWhere("status=0 and BINARY login_name='" +user.getLoginName()+"'");
        User first = Utils.getFirst(userService.query(example));
        If.of(first != null).isTrueThrow(() -> new AppException(ErrorCode.USER_WAS_EXISTS));
        userService.create(user);
        return SimpleResponse.success(user);
    }

    @ApiOperation("修改User")
    @PutMapping
    public SimpleResponse<User> update(@RequestBody User user) {
        //根据id修改
        userService.update(user);
        return SimpleResponse.success(user);
    }

    @ApiOperation("删除User")
    @DeleteMapping("/{id}")
    public SimpleResponse delete(@PathVariable("id") Integer id) {
        userService.deleteById(id);
        return SimpleResponse.success();
    }

}
