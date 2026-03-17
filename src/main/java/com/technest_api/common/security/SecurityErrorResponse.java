package com.technest_api.common.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class SecurityErrorResponse {
    private SecurityErrorResponse() {
    }

    public static void unauthorized(HttpServletRequest request, HttpServletResponse response,
                                    String message) throws IOException {
        defineError(response, 401, message, request.getRequestURI(), request.getMethod());
    }

    public static void accessDenied(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        defineError(response, 403, "Access Denied", request.getRequestURI(), request.getMethod());
    }

    private static void defineError(HttpServletResponse response, int status, String message,
                                    String path, String method) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter()
                .write("""
                        {"status": %d, "message": "%s", "path": "%s","method": "%s"}
                        """.formatted(status, message, path, method));
    }
}
