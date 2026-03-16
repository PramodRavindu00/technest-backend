package com.technest_api.common.security;

import com.technest_api.module.user.UserRepository;
import com.technest_api.module.user.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // extract jwt token from the request auth headers
        String token = extractTokenFromRequest(request);
        if (token == null) {
            // continue as an anonymous user
            filterChain.doFilter(request, response);
            return;
        }

        // token validations
        if (jwtService.isTokenExpired(token)) {
            SecurityErrorResponse.unauthorized(request, response, "Token has expired");
            return;
        }

        if (!jwtService.isTokenValid(token)) {
            SecurityErrorResponse.unauthorized(request, response, "Invalid Token");
            return;
        }

        String userIdFromToken = "";
        try {
            // extract userId from the token claims
            userIdFromToken = jwtService.extractUserIdFromToken(token);
        }
        catch (Exception e) {
            SecurityErrorResponse.unauthorized(request, response, "Invalid Token");
        }

        // get the user from the database using the userId from the token
        User user = userRepository.findById(UUID.fromString(userIdFromToken))
                .orElse(null);

        // this checks a valid token exists but user deleted scenario
        if (user == null) {
            SecurityErrorResponse.unauthorized(request, response, "User not found");
            return;
        }

        // build the authenticated user from the db response
        AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
                .id(String.valueOf(user.getId()))
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        // populate security context
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authenticatedUser, null,
                        List.of(new SimpleGrantedAuthority(authenticatedUser.getRole()
                                .name())));
        SecurityContextHolder.getContext()
                .setAuthentication(authenticationToken);

        // proceed to the security config with security context loaded with an authenticated user
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String requestAuthHeader = request.getHeader("Authorization");
        if (requestAuthHeader == null || !requestAuthHeader.startsWith("Bearer ")) {
            return null;
        }
        return requestAuthHeader.substring(7);
    }
}
