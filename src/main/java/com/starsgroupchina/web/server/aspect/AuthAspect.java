package com.starsgroupchina.web.server.aspect;

import com.starsgroupchina.common.annotation.AuthIgnore;
import com.starsgroupchina.common.context.Context;
import com.starsgroupchina.common.context.ContextHolder;
import com.starsgroupchina.common.exception.AppException;
import com.starsgroupchina.web.server.ErrorCode;
import com.starsgroupchina.web.server.bean.AuthMember;
import com.starsgroupchina.web.server.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Created by zhangfeng on 2018-6-5.
 */
@Aspect
@Component
public class AuthAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserService userService;

    @Before("execution(* com.starsgroupchina.web.server.controller.management..*.*(..))")
    public void authorization(JoinPoint point) {
        //当包含AuthIgnore注解时不做登陆校验
        for (Object arg : ((MethodSignature) point.getSignature()).getMethod().getAnnotations()) {
            if (arg instanceof AuthIgnore)
                return;
        }
        setContext();
    }

    private void setContext() {
        String token = request.getHeader("token");
        AuthMember authMember = Optional.ofNullable(userService.getAuthMember(token))
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_ERROR));
        Context context = new Context(authMember);
        ContextHolder.setContext(context);
        userService.refreshAuthMember(token);
    }

}
