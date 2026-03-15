package com.community.aspect;


import com.community.annotation.GlobalInterceptor;
import com.community.client.KeyRotationListener;
import com.community.constant.TimeConstant;
import com.community.entity.pojo.SecretKey;
import com.community.entity.pojo.UserContext;
import com.community.entity.pojo.UserInfo;
import com.community.enums.ResponseCodeEnum;
import com.community.enums.UserRoleEnum;
import com.community.exception.BusinessException;
import com.community.service.RedisService;
import com.community.util.JwtUtil;
import com.community.util.RedisUtils;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: GlobalOperationAspect
 * Package: com.easymeeting.aspect
 * Description:
 *
 * @Author wth
 * @Create 2025/9/13 23:23
 * @Version 1.0
 */
@Aspect
@Component
@Slf4j
public class GlobalOperationAspect {
    @Resource
    private RedisService redisService;
    @Resource
    private KeyRotationListener keyRotationListener;
    @Before("@annotation(com.community.annotation.GlobalInterceptor)")
    public void interceptorDo(JoinPoint point){
        try{
            Method method = ((MethodSignature)point.getSignature()).getMethod();
            GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);
            if(interceptor==null){
                return;
            }
            if(interceptor.checkLogin()|| interceptor.checkSpecial() || interceptor.checkAdmin()){
                checkLogin(interceptor.checkSpecial(), interceptor.checkAdmin());
            }
        }catch (BusinessException e){
            throw e;
        }catch (Exception e){
            log.error("全局拦截器异常",e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }

    }
    private void checkLogin(Boolean checkSpecial, Boolean checkAdmin){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String kid = request.getHeader("kid");
        String accessToken = request.getHeader("access_token");
        String refreshToken = request.getHeader("refresh_token");
        if(kid==null||accessToken==null||refreshToken==null){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        String key = keyRotationListener.getKeyById( kid).toString();
        if(key==null){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        Claims claims = JwtUtil.parseJWT(key, accessToken);
        Date expiration =claims.getExpiration();
        boolean needRefresh = false;
        UserInfo userInfo;
        if(expiration.before(new Date())){
            userInfo = redisService.getUserInfoByRefreshToken(refreshToken);
            if(userInfo==null){
                throw new BusinessException(ResponseCodeEnum.CODE_901);
            }else{
                needRefresh = true;
            }
        }else{
            userInfo = new UserInfo();
            userInfo.setRole(claims.get("role",Integer.class));
        }
        if(needRefresh){
            Map<String,String> result = generateNewAccessToken(key, userInfo);
            String newAccessToken = result.get("access_token");
            String newKid = result.get("kid");
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            if (response != null) {
                response.setHeader("access_token", newAccessToken);
                response.setHeader("refresh_token", refreshToken);
                response.setHeader("kid", newKid);
            }
        }
        if(checkSpecial){
            if(!UserRoleEnum.SPECIAL.getStatus().equals(userInfo.getRole())){
                throw new BusinessException("权限不足");
            }
        }
        if(checkAdmin){
            if(!UserRoleEnum.ADMIN.getStatus().equals(userInfo.getRole())){
                throw new BusinessException("权限不足");
            }
        }
        UserContext.setUserInfo(userInfo);
    }
    private Map<String,String> generateNewAccessToken(String key, UserInfo userInfo) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userInfo.getUserId());
        claims.put("userName", userInfo.getUserName());
        claims.put("role", userInfo.getRole());
        SecretKey secretKey = new SecretKey(keyRotationListener.getCurrentKeyIdWithCache(),keyRotationListener.getCurrentKeyWithCache().toString());
        String accessToken = JwtUtil.createJWT(secretKey, TimeConstant.TWO_HOURS_OF_SECONDS, claims);
        Map<String,String> result = new HashMap<>();
        result.put("access_token", accessToken);
        result.put("kid",secretKey.getId());
        return result;
    }
}
