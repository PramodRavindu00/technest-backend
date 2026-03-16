package com.technest_api.common.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class SecurityErrorResponse {
    private SecurityErrorResponse() {
    }

    public static void unauthorized(HttpServletRequest request, HttpServletResponse response,
                                    String message) throws IOException {
        defineError(response, 401, message, request.getRequestURI());
    }

    public static void accessDenied(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        defineError(response, 403, "Access Denied", request.getRequestURI());
    }

    private static void defineError(HttpServletResponse response, int status, String message,
                                    String path) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter()
                .write("""
                        {"status": %d, "message": "%s", "path": "%s"}
                        """.formatted(status, message, path));
    }
}
