package com.technest_api.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LogoutInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response, @NonNull Object handler,
                                Exception ex) {

        // clear refresh token cookie
        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", null)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth/refresh")  // match exactly how it was set
                .maxAge(0)              // deletes the cookie
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        // clear security context
        SecurityContextHolder.clearContext();
    }
}
