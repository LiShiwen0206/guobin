package com.starsgroupchina.web.server;

import com.starsgroupchina.common.exception.ErrorMessage;

public class ErrorCode {


    @ErrorMessage("用户名或密码不正确")
    public static final int AUTH_USER_PWD = 1401;

    @ErrorMessage("获取用户信息失败，请登录后重试")
    public static final int AUTH_ERROR = 1402;

    @ErrorMessage("用户名或密码错误")
    public static final int AUTH_ILLEGAL_USER_PWD = 1403;
    @ErrorMessage("图片验证码不正确")
    public static final int IMAGE_CAPTCHA_ERROR = 1404;
    @ErrorMessage("图片验证码不存在或已过期")
    public static final int IMAGE_CAPTCHA_EXPIRED = 1405;

    @ErrorMessage("项目编号[{0}]的项目不存在")
    public static final int PROJECT_NOT_EXIST = 3001;

    @ErrorMessage("该user已存在")
    public static final int USER_WAS_EXISTS = 4005;
}
