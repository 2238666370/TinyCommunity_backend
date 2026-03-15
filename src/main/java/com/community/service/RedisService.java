package com.community.service;

import com.community.entity.pojo.UserInfo;

/**
 * ClassName: RedisService
 * Package: com.community.service
 * Description:
 *
 * @Author wth
 * @Create 2026/3/12 0:41
 * @Version 1.0
 */
public interface RedisService {
    String saveCheckCode(String code);

    String getCheckCode(String key);

    void deleteRefreshToken(String refreshToken);

    UserInfo getUserInfoByRefreshToken(String refreshToken);
}
