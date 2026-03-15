package com.community.aspect;

import com.community.entity.pojo.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * ClassName: ClearThreadLocalInterceptor
 * Package: com.community.aspect
 * Description:
 *
 * @Author wth
 * @Create 2026/3/15 13:02
 * @Version 1.0
 */
// 创建拦截器清理ThreadLocal（可选但推荐）
@Component
public class ClearThreadLocalInterceptor implements HandlerInterceptor {
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
