package com.technest_api.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String path = request.getRequestURI();
        String method = request.getMethod();
        boolean completed = false;
        try {
            filterChain.doFilter(request, response);
            completed = true;
        }
        finally {
            long duration = System.currentTimeMillis() - startTime;
            int statusCode = response.getStatus();
            logger.info("[{}] {} | Status: {} | Completed: {} | Time: {}ms", method, path,
                    statusCode, completed, duration);

        }
    }
}
