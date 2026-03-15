package com.community.controller;

import com.community.constant.RedisConstant;
import com.community.constant.TimeConstant;
import com.community.entity.pojo.CaptchaResult;
import com.community.entity.vo.CheckCodeVO;
import com.community.entity.vo.ResponseVO;
import com.community.entity.vo.UserInfoVO;
import com.community.enums.ResponseCodeEnum;
import com.community.exception.BusinessException;
import com.community.service.RedisService;
import com.community.service.UserInfoService;
import com.community.util.CaptchaGenerator;
import com.community.util.RedisUtils;
import com.community.util.SnowflakeUtil;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ClassName: UserController
 * Package: com.community.controller
 * Description:
 *
 * @Author wth
 * @Create 2026/3/10 23:48
 * @Version 1.0
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class AccountController extends ABaseController {
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private RedisService redisService;

    @RequestMapping("/checkCode")
    public ResponseVO checkCode() {
        CaptchaResult captchaResult = CaptchaGenerator.generate();
        String code = captchaResult.getCode();
        String captcha;
        try {
            captcha = captchaResult.toBase64();
        } catch (Exception e) {
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }
        String key = redisService.saveCheckCode(code);
        CheckCodeVO checkCodeVO = new CheckCodeVO();
        checkCodeVO.setCaptcha(captcha);
        checkCodeVO.setKey(key);
        checkCodeVO.setTimestamp(System.currentTimeMillis());
        log.info("获取图片验证码");
        return this.getSuccessResponseVO(checkCodeVO);
    }

    @RequestMapping("/login")
    public ResponseVO login(@NotEmpty String key,
                            @NotEmpty String email,
                            @NotEmpty String password,
                            @NotEmpty String code) {
        String checkCode = redisService.getCheckCode(key);
        if (!code.equals(checkCode)) {
            throw new BusinessException("验证码错误");
        }
        UserInfoVO userInfoVO = userInfoService.login(email, password);
        return this.getSuccessResponseVO(userInfoVO);
    }

    @RequestMapping("/register")
    public ResponseVO register(@NotEmpty String key,
                               @NotEmpty String email,
                               @NotEmpty String password,
                               @NotEmpty String userName,
                               @NotEmpty String code) {
        String checkCode = redisService.getCheckCode(key);
        if (!code.equals(checkCode)) {
            throw new BusinessException("验证码错误");
        }
        Boolean isSuccess = userInfoService.register(email, password, userName);
        return this.getSuccessResponseVO(isSuccess);
    }

    @RequestMapping("/logout")
    public ResponseVO logout(@NotEmpty String refreshToken) {
        //TODO 记录用户是否上线，可以用websocket或者http轮询检测
        redisService.deleteRefreshToken(refreshToken);
        return this.getSuccessResponseVO(null);
    }
}
