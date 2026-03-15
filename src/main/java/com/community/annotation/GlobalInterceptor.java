package com.community.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * ClassName: GlobalInterceptor
 * Description:
 *
 * @Author wth
 * @Create 2025/9/13 23:12
 * @Version 1.0
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface GlobalInterceptor {

    boolean checkLogin() default true;
    boolean checkSpecial() default false;
    boolean checkAdmin() default false;
}
