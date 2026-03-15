package com.technest_api.common.advice;

import com.technest_api.common.annotation.SetRefreshTokenCookie;
import com.technest_api.module.auth.dto.AuthResponse;
import com.technest_api.module.auth.dto.AuthTokens;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice

public class RefreshTokenCookieAdvice implements ResponseBodyAdvice<Object> {

    @Value("${app.cookie.refresh-token-expiration-sec}")
    private long refreshTokenCookieExpiration;

    @Value("${app.cookie.http-secure:false}")
    private Boolean isCookieHttpSecure;

    @Override
    public boolean supports(MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(SetRefreshTokenCookie.class);
    }

    @Override
    public AuthResponse beforeBodyWrite(@Nullable Object body, @NonNull MethodParameter returnType,
                                        @NonNull MediaType selectedContentType,
                                        @NonNull Class<? extends HttpMessageConverter<?>> converterType,
                                        @NonNull ServerHttpRequest request,
                                        @NonNull ServerHttpResponse response) {
        if (body instanceof AuthTokens tokens) {
            if (tokens.getRefreshToken() != null) {
                HttpCookie cookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                        .httpOnly(true)
                        .secure(isCookieHttpSecure)
                        .sameSite("Strict")
                        .path("/auth/refresh")
                        .maxAge(refreshTokenCookieExpiration)
                        .build();
                response.getHeaders()
                        .add(HttpHeaders.SET_COOKIE, cookie.toString());
            }
            if (tokens.getAccessToken() != null) {
                return new AuthResponse(tokens.getAccessToken());
            }
        }
        return null;
    }
}
