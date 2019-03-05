package com.starsgroupchina.web.server.controller.management;

import com.starsgroupchina.common.annotation.AuthIgnore;
import com.starsgroupchina.common.exception.AppException;
import com.starsgroupchina.common.response.SimpleResponse;
import com.starsgroupchina.common.utils.Utils;
import com.starsgroupchina.web.server.ErrorCode;
import com.starsgroupchina.web.server.KeyCache;
import com.starsgroupchina.web.server.RedisConf;
import com.starsgroupchina.web.server.bean.AuthMember;
import com.starsgroupchina.web.server.bean.model.User;
import com.starsgroupchina.web.server.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Created by zhangfeng on 2018/10/25
 */
@Slf4j
@Api
@RestController
@RequestMapping(value = "/management", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class LoginController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserService userService;

    @Resource(name = RedisConf.REDIS_TEMPLATE)
    private RedisOperations redisOperations;

    @ApiResponses({@ApiResponse(code = ErrorCode.AUTH_USER_PWD, message = "用户名或密码不正确")})
    @AuthIgnore
    @ApiOperation("登录")
    @GetMapping("/login")
    public SimpleResponse<AuthMember> login(
            @RequestParam(value = "name", defaultValue = "admin") String name,
            @RequestParam(value = "pwd", defaultValue = "E10ADC3949BA59ABBE56E057F20F883E") String pwd) {
        User user = Optional.ofNullable(userService.getUser(name.trim(), pwd))
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_USER_PWD));
        String token = Utils.getUUID();
        AuthMember authMember = userService.cacheAuthMember(token, user);
        return SimpleResponse.success(authMember);
    }

    @AuthIgnore
    @ApiOperation("登出")
    @GetMapping("/logout")
    public SimpleResponse logout(){
        String token = request.getHeader("token");
        redisOperations.delete(KeyCache.token(token));
        return SimpleResponse.success();
    }
}
