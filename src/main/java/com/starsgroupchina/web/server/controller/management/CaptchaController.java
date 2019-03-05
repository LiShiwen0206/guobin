package com.starsgroupchina.web.server.controller.management;

import com.starsgroupchina.common.annotation.AuthIgnore;
import com.starsgroupchina.common.exception.AppException;
import com.starsgroupchina.common.response.SimpleResponse;
import com.starsgroupchina.common.utils.CaptchaUtil;
import com.starsgroupchina.web.server.ErrorCode;
import com.starsgroupchina.web.server.KeyCache;
import com.starsgroupchina.web.server.RedisConf;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 */
@Slf4j
@Api
@RestController
@RequestMapping(value = "/management", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CaptchaController {
    //图片验证码过期时间
    private int expireSeconds = 60;

    private CaptchaUtil captchaUtil = new CaptchaUtil();

    @Resource(name = RedisConf.REDIS_TEMPLATE)
    private RedisOperations redisOperations;

    @ApiResponses({
            @ApiResponse(code = ErrorCode.AUTH_ILLEGAL_USER_PWD, message = "用户名或密码错误"),
            @ApiResponse(code = ErrorCode.IMAGE_CAPTCHA_ERROR, message = "图片验证码不正确"),
            @ApiResponse(code = ErrorCode.IMAGE_CAPTCHA_EXPIRED, message = "图片验证码不存在或已过期")
    })
    @ApiOperation("获图片取验证码")
    @GetMapping("/image/{captchaId}")
    @AuthIgnore
    public void getCaptcha(@ApiParam("客户端生成的随机数") @PathVariable("captchaId") String captchaId,
                           HttpServletResponse response) throws IOException {

        //根据随机数生成验证码
        String text = captchaUtil.createImageCaptcha(captchaId);
        //验证码存入redis
        redisOperations.opsForValue().set(KeyCache.getImageCaptchaKey(captchaId), text, expireSeconds, TimeUnit.SECONDS);
        //图片验证码输出前端
        captchaUtil.responseOutCaptcha(text, response);
    }

    @ApiResponses({
            @ApiResponse(code = ErrorCode.IMAGE_CAPTCHA_ERROR, message = "图片验证码不正确"),
            @ApiResponse(code = ErrorCode.IMAGE_CAPTCHA_EXPIRED, message = "图片验证码不能存在或已过期")
    })
    @ApiOperation("校验图片取验证码")
    @GetMapping("/image/{captchaId}/check")
    @AuthIgnore
    public SimpleResponse checkCaptcha(@ApiParam("客户端生成的随机数") @PathVariable("captchaId") String captchaId,
                                       @ApiParam("验证码") @RequestParam("captcha") String captcha) {
        String captchaCache = (String) redisOperations.opsForValue().get(KeyCache.getImageCaptchaKey(captchaId));
        if (StringUtils.isBlank(captchaCache)) {
            throw new AppException(ErrorCode.IMAGE_CAPTCHA_EXPIRED);
        } else if (!captchaCache.equalsIgnoreCase(captcha)) {
            throw new AppException(ErrorCode.IMAGE_CAPTCHA_ERROR);
        }
        //redisOperations.delete(KeyCache.getImageCaptchaKey(captchaId));
        return SimpleResponse.success();
    }

}
