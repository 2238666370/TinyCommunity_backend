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
    public static final String JWT_KEY_PREFIX = REDIS_KEY_PREFIX +"jwt:secret:";
    public static final String CURRENT_KEY = JWT_KEY_PREFIX + "current";
    public static final String PREVIOUS_KEY = JWT_KEY_PREFIX + "previous";
    public static final String KEY_ID = JWT_KEY_PREFIX + "kid";
    public static final String ROTATION_LOCK = JWT_KEY_PREFIX + "lock";
    public static final String LAST_ROTATION = JWT_KEY_PREFIX + "rotation_time";
    public static final String ROTATION_SCHEDULED_LOCK = JWT_KEY_PREFIX +"scheduled:lock";
    public static final String JWT_KEY_COUNTER = "jwt:key:counter";
}
