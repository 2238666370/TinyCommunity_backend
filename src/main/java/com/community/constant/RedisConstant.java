package com.community.constant;

/**
 * ClassName: RedisConstant
 * Package: com.community.constant
 * Description:
 *
 * @Author wth
 * @Create 2026/3/11 20:11
 * @Version 1.0
 */
public class RedisConstant {
    private static final String REDIS_KEY_PREFIX = "community:";
    public static final String CAPTCHA_KEY_PREFIX = REDIS_KEY_PREFIX + "captcha:";
    public static final String REFRESH_TOKEN_KEY_PREFIX = REDIS_KEY_PREFIX + "refreshToken:";
}
