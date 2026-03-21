package com.technest_api.module.auth.oAuth;

import com.technest_api.common.exception.OAuth2AuthenticationException;
import com.technest_api.module.auth.AuthService;
import com.technest_api.module.user.UserService;
import com.technest_api.module.user.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${app.cors.origin}")
    private String origin;

    private final UserService userService;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull Authentication authentication) throws IOException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            if (oAuth2User == null) {
                throw OAuth2AuthenticationException.principalIsNull();
            }
            String googleId = oAuth2User.getAttribute("sub");
            String email = oAuth2User.getAttribute("email");
            if (googleId == null || email == null) {
                throw OAuth2AuthenticationException.missingAttributes(googleId, email);
            }

            // find or create a user record
            User user = userService.findOrCreate(googleId, email);

            // generate auth code and send the code to redis database with authenticated user's ID
            String authCode = authService.generateAuthCode(user);

            // redirect to the browser
            response.sendRedirect(origin + "/oauth2/callback?code=" + authCode);
        }
        catch (OAuth2AuthenticationException ex) {
            response.sendRedirect(origin + "/login?error=" + ex.getErrorCode());
        }
        catch (Exception ex) {
            log.error("OAuth2 login failed unexpectedly", ex);
            response.sendRedirect(origin + "/login?error=server_error");
        }
    }
}
