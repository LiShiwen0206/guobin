package com.starsgroupchina.web.server.service;

import com.starsgroupchina.common.base.AbstractService;
import com.starsgroupchina.common.context.Context;
import com.starsgroupchina.common.context.ContextHolder;
import com.starsgroupchina.common.utils.JsonUtil;
import com.starsgroupchina.common.utils.Utils;
import com.starsgroupchina.web.server.KeyCache;
import com.starsgroupchina.web.server.RedisConf;
import com.starsgroupchina.web.server.bean.AuthMember;
import com.starsgroupchina.web.server.bean.mapper.UserMapper;
import com.starsgroupchina.web.server.bean.model.User;
import com.starsgroupchina.web.server.bean.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserService extends AbstractService<User, UserExample> {

    //30分钟过期
    final static Integer EXPIRE = 30;
    final static TimeUnit TIME_UNIT = TimeUnit.MINUTES;

    @Resource(name = RedisConf.REDIS_TEMPLATE)
    private RedisOperations redisOperations;

    @Autowired
    private UserMapper userMapper;

    public User getUser(String loginName, String loginPwd) {
        UserExample example = new UserExample();
        example.setAdditionalWhere("status=0 and BINARY login_name='" + loginName + "' and password='" + loginPwd + "'");
        List<User> users = userMapper.selectByExample(example);
        return Utils.getFirst(users);
    }

    public AuthMember cacheAuthMember(String token, User user) {
        AuthMember authMember = new AuthMember(token, user);
        redisOperations.opsForValue().set(KeyCache.token(token), JsonUtil.toJson(authMember), EXPIRE, TIME_UNIT);
        return authMember;
    }

    public AuthMember getAuthMember(String token) {
        Object authMember = redisOperations.opsForValue().get(KeyCache.token(token));
        if (authMember == null) return null;
        return JsonUtil.toObject(authMember.toString(), AuthMember.class);
    }

    public void refreshAuthMember(String token) {
        redisOperations.expire(KeyCache.token(token), EXPIRE, TIME_UNIT);
    }

}
