package com.community.service.impl;

import com.community.constant.RedisConstant;
import com.community.constant.TimeConstant;
import com.community.dao.UserInfoDao;
import com.community.entity.pojo.UserInfo;
import com.community.entity.vo.UserInfoVO;
import com.community.enums.UserStatusEnum;
import com.community.exception.BusinessException;
import com.community.service.UserInfoService;
import com.community.util.JwtUtil;
import com.community.util.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
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
    @Override
    public UserInfoVO login(String email, String password) {
        //获取用户信息
        UserInfo userInfo = userInfoDao.selectByEmail(email);
        //校验
        Integer salt = userInfo.getSalt();
        String encodePassword = DigestUtils.md5DigestAsHex((password + salt).getBytes());
        if(!encodePassword.equals(userInfo.getPassword())){
            throw new BusinessException("账号或密码错误");
        }
        if(UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())){
            throw new BusinessException("用户被禁用");
        }
        //生成双Token
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId", userInfo.getUserId());
        claims.put("userName", userInfo.getUserName());
        claims.put("role",userInfo.getRole());
        String accessToken = JwtUtil.createJWT(userInfo.getUserId().toString(), TimeConstant.TWO_HOURS_OF_SECONDS, claims);
        String refreshToken = UUID.randomUUID().toString();
        String refreshTokenKey = RedisConstant.REFRESH_TOKEN_KEY_PREFIX + refreshToken;
        String refreshTokenValue = userInfo.getUserId().toString(); // 存储用户ID
        redisUtils.setex(refreshTokenKey, refreshTokenValue, TimeConstant.SEVEN_DAYS_OF_SECONDS);
        //TODO 记录登录信息
        //更新活跃时间
        userInfo.setLastLoginTime(System.currentTimeMillis());
        userInfoDao.updateById(userInfo);
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
}
