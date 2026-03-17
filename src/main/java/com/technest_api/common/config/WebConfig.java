package com.technest_api.common.config;

import com.technest_api.common.interceptor.ApiLoggingInterceptor;
import com.technest_api.common.interceptor.LogoutInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ApiLoggingInterceptor apiLoggingInterceptor;
    private final LogoutInterceptor logoutInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiLoggingInterceptor)
                .addPathPatterns("/**"); // intercept all API requests
        registry.addInterceptor(logoutInterceptor)
                .addPathPatterns("/auth/logout");  // only for logout route
    }
}