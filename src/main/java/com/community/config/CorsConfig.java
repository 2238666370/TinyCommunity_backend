package com.community.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许前端地址访问，生产环境建议写具体域名，不要用*
        config.addAllowedOrigin("http://localhost:5173");
        // 允许携带Cookie等凭证（记住我功能需要）
        config.setAllowCredentials(true);
        // 允许所有请求方法（GET/POST/OPTIONS等）
        config.addAllowedMethod("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 预检请求的有效期，单位秒，有效期内不会重复发起OPTIONS预检
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有接口生效
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}