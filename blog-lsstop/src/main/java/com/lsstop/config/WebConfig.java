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
 * @author lsstop
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AdminAuthInterceptor adminAuthInterceptor;
    private final FrontAuthInterceptor frontAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 后台拦截器 - 拦截所有含 /admin/ 的路径
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/**/admin/**");

        // 前台拦截器 - 只拦截需要登录的前台接口
        registry.addInterceptor(frontAuthInterceptor)
                .addPathPatterns(
                        // 需要登录的前台接口在这里添加
                        "/user/**"
                )
                .excludePathPatterns("/**/admin/**");
    }
}
