package com.community.service.impl;

import com.community.client.KeyRotationListener;
import com.community.constant.RedisConstant;
import com.community.constant.TimeConstant;
import com.community.dao.UserInfoDao;
import com.community.entity.pojo.SecretKey;
import com.community.entity.pojo.UserInfo;
import com.community.entity.vo.UserInfoVO;
import com.community.enums.UserRoleEnum;
import com.community.enums.UserSexEnum;
import com.community.enums.UserStatusEnum;
import com.community.exception.BusinessException;
import com.community.service.UserInfoService;
import com.community.util.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * ClassName: UserInfoServiceImpl
 * Package: com.community.service.impl
 * Description:
 *
 * @Author wth
 * @Create 2026/3/10 23:59
 * @Version 1.0
 */
@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {
    @Resource
    private UserInfoDao userInfoDao;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private KeyRotationListener keyRotationListener;

    @Override
    public UserInfoVO login(String email, String password) {
        //获取用户信息
        UserInfo userInfo = userInfoDao.selectByEmail(email);
        //校验
        Integer salt = userInfo.getSalt();
        String encodePassword = DigestUtils.md5DigestAsHex((password + salt).getBytes());
        if (!encodePassword.equals(userInfo.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        if (UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())) {
            throw new BusinessException("用户被禁用");
        }
        //生成双Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userInfo.getUserId());
        claims.put("userName", userInfo.getUserName());
        claims.put("role", userInfo.getRole());
        SecretKey secretKey = new SecretKey(keyRotationListener.getCurrentKeyIdFromRedis(),keyRotationListener.getCurrentKeyWithCache().toString());
        String accessToken = JwtUtil.createJWT(secretKey,TimeConstant.TWO_HOURS_OF_SECONDS, claims);
        String refreshToken = RandomUtil.generateUUID();
        String refreshTokenKey = RedisConstant.REFRESH_TOKEN_KEY_PREFIX + refreshToken;
        String refreshTokenValue = JsonUtil.toJsonSilently(userInfo);//记录用户信息
        redisUtils.setex(refreshTokenKey, refreshTokenValue, TimeConstant.SEVEN_DAYS_OF_SECONDS);
        //TODO 记录登录信息
        //更新上次登录时间
        userInfo.setLastLoginTime(System.currentTimeMillis());
        userInfoDao.updateByUserId(userInfo);
        //生成返回信息
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUserId(userInfo.getUserId());
        userInfoVO.setEmail(userInfo.getEmail());
        userInfoVO.setSex(userInfo.getSex());
        userInfoVO.setUserName(userInfo.getUserName());
        userInfoVO.setPhoto(userInfo.getPhoto());
        userInfoVO.setAccessToken(accessToken);
        userInfoVO.setRefreshToken(refreshToken);
        return userInfoVO;
    }

    @Override
    public Boolean register(String email, String password, String userName) {
        UserInfo userInfo = userInfoDao.selectByEmail(email);
        if (userInfo != null) {
            throw new BusinessException("邮箱已存在");
        }
        userInfo = userInfoDao.selectByUserName(userName);
        if (userInfo != null) {
            throw new BusinessException("用户名已存在");
        }
        if (userName.length() > 9) {
            throw new BusinessException("用户名过长");
        }
        if (!PasswordUtil.checkPassword(password)) {
            throw new BusinessException("密码过于简单");
        }
        userInfo = new UserInfo();
        userInfo.setEmail(email);
        Integer salt = RandomUtil.generateNumberLowerThan10();
        userInfo.setSalt(salt);
        String encodePassword = DigestUtils.md5DigestAsHex((password + salt).getBytes());
        userInfo.setPassword(encodePassword);
        userInfo.setUserName(userName);
        userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
        userInfo.setCreateTime(System.currentTimeMillis());
        userInfo.setLastLoginTime(System.currentTimeMillis());
        userInfo.setSex(UserSexEnum.UNKOWN.getStatus());
        userInfo.setRole(UserRoleEnum.NORMAL.getStatus());
        return userInfoDao.save(userInfo);
    }

}
