package com.technest_api.module.auth.oAuth;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {
    @Value("${app.cors.origin}")
    private String origin;

    @Override
    public void onAuthenticationFailure(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull AuthenticationException exception)
            throws IOException {
        log.error("OAuth2 authentication failed - {}", exception.getMessage());
        if (exception.getCause() != null) {
            log.error("Cause: {}", exception.getCause()
                    .getMessage());
        }

        // Log the full exception
        log.error("Full exception: ", exception);
        response.sendRedirect(origin + "/login?error=oauth_failed");
    }
}

