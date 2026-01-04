package com.lsstop.config;

import com.lsstop.interceptor.AdminAuthInterceptor;
import com.lsstop.interceptor.FrontAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AdminAuthInterceptor adminAuthInterceptor;
    private final FrontAuthInterceptor frontAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 后台拦截器 - 拦截 /admin/** 路径，必须登录且使用admin token
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/admin/**");

        // 前台拦截器 - 拦截需要登录的 /front/** 路径
        registry.addInterceptor(frontAuthInterceptor)
                .addPathPatterns("/front/like/**",
                        "/front/message/addMessage",
                        "/front/user/info");
    }
}
