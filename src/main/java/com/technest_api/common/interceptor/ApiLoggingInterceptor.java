package com.technest_api.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ApiLoggingInterceptor.class);
    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response, @NonNull Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response, @NonNull Object handler,
                                Exception ex) {

        Long startTime = (Long) request.getAttribute(START_TIME);
        if (startTime == null) {
            return;
        }

        long duration = System.currentTimeMillis() - startTime;

        String method = request.getMethod();
        String path = request.getRequestURI();
        int status = response.getStatus();

        String message = String.format("%s %s - %d - %dms", method, path, status, duration);

        if (status >= 500) {
            log.error(message);
        } else if (status >= 400) {
            log.warn(message);
        } else {
            log.info(message);
        }
    }
}