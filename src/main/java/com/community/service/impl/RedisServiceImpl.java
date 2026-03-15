package com.community.service.impl;

import com.community.constant.RedisConstant;
import com.community.constant.TimeConstant;
import com.community.enums.ResponseCodeEnum;
import com.community.exception.BusinessException;
import com.community.service.RedisService;
import com.community.util.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * ClassName: RedisServiceImpl
 * Package: com.community.service.impl
 * Description:
 *
 * @Author wth
 * @Create 2026/3/12 0:41
 * @Version 1.0
 */
@Service
@Slf4j
public class RedisServiceImpl implements RedisService {
    @Resource
    private RedisUtils redisUtils;
    @Override
    public String saveCheckCode(String code){
        String key = UUID.randomUUID().toString().replace("-", "");
        boolean res = redisUtils.setex(RedisConstant.CAPTCHA_KEY_PREFIX + key, code, TimeConstant.THREE_MINUTES_OF_SECONDS);
        if(!res){
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }
        return key;
    }

    @Override
    public String getCheckCode(String key) {
        String code = (String) redisUtils.get(RedisConstant.CAPTCHA_KEY_PREFIX + key);
        redisUtils.delete(RedisConstant.CAPTCHA_KEY_PREFIX + key);
        return code;
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        redisUtils.delete(RedisConstant.REFRESH_TOKEN_KEY_PREFIX + refreshToken);
    }
}
