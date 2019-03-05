package com.starsgroupchina.web.server;

/**
 * Created by zhangfeng on 2018-5-31.
 */
public class KeyCache {

    private static final String KEY_ROOT_PREFIX = "guobin:";

    private static final String TOKEN = KEY_ROOT_PREFIX + "token:%s";

    //=======================================================

    public static final String CACHE_BANNER = KEY_ROOT_PREFIX + "cache:banner";

    public static final String CACHE_ARTICLE = KEY_ROOT_PREFIX + "cache:article";


    public static final String token(String token) {
        return String.format(TOKEN, token);
    }


    private static final String KEY_PATTERN_IMAGE_CAPTCHA = KEY_ROOT_PREFIX + "image_captcha:%s";

    public static String getImageCaptchaKey(String captchaId) {
        return String.format(KEY_PATTERN_IMAGE_CAPTCHA, captchaId);
    }



}
