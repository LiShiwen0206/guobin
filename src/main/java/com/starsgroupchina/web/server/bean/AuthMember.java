package com.starsgroupchina.web.server.bean;

import com.starsgroupchina.web.server.bean.model.User;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhangfeng on 2018/10/25
 */
@Data
public class AuthMember implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token;

    private User user;

    public AuthMember() {
    }

    public AuthMember(String token,User user) {
        this.token = token;
        this.user = user;
    }
}
