package com.technest_api.module.auth;

import com.technest_api.common.constant.enums.Role;
import com.technest_api.common.service.JwtService;
import com.technest_api.module.auth.dto.AuthTokens;
import com.technest_api.module.auth.dto.LoginRequest;
import com.technest_api.module.auth.dto.SignUpRequest;
import com.technest_api.module.user.UserService;
import com.technest_api.module.user.dto.CreateUserDto;
import com.technest_api.module.user.model.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;

    public void localSignUp(SignUpRequest dto) {
        Optional<User> existByEmail = userService.findByEmail(dto.getEmail());
        if (existByEmail.isPresent()) {
            User userByEmail = existByEmail.get();
            if (userByEmail.getPasswordHash() == null) {
                String connectedOauthProviders = getConnectedOauthProviders(userByEmail);
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Email already exists with " + connectedOauthProviders + " login");
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        CreateUserDto newUser = new CreateUserDto(dto);
        userService.createUser(newUser);
    }

    public AuthTokens localLogin(LoginRequest dto) {
        Optional<User> existingUserByEmail = userService.findByEmail(dto.getEmail());
        if (existingUserByEmail.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        User exisitngUser = existingUserByEmail.get();
        String currentPasswordHash = exisitngUser.getPasswordHash();
        if (currentPasswordHash == null) {
            String connectedOauthProviders = getConnectedOauthProviders(exisitngUser);
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No password set for this account. You previously signed in with " +
                            connectedOauthProviders + ". Please use that or reset your password.");
        }
        boolean isPasswordMatching =
                passwordEncoder.matches(dto.getPassword(), currentPasswordHash);
        if (!isPasswordMatching) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return generateTokensFromVerifiedUser(exisitngUser);
    }

    public AuthTokens refresh(HttpServletRequest request) {
        String refreshToken = extractCookieAndReturnToken(request);
        if (refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Refresh token not found. " + "Please login");
        }
        if (jwtService.isTokenExpired(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Refresh token has expired" + ". Please login");
        }

        if (jwtService.isTokenValid(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid refresh token. " + "Please login");
        }

        String userIdFromToken = jwtService.extractUserIdFromToken(refreshToken);

        User user = userService.findById(userIdFromToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "User not found"));
        return generateTokensFromVerifiedUser(user);
    }

    // should implement
//    public AuthTokens exchange(AuthCodeExchangeRequest dto) {
//
//        return generateTokensFromVerifiedUser();
//    }

    public String generateAuthCode(User authenticatedUser) {
        //need to implement
        String authCode = UUID.randomUUID()
                .toString();
        return authCode;
    }

    private String getConnectedOauthProviders(User user) {
        List<String> oauthProviders = new ArrayList<>();
        if (user.getGoogleId() != null) oauthProviders.add("Google");
        if (user.getLinkedInId() != null) oauthProviders.add("LinkedIn");
        return oauthProviders.isEmpty() ? "an external provider" :
                String.join(" and ", oauthProviders);
    }

    private AuthTokens generateTokensFromVerifiedUser(User verfiedUser) {
        // get values needed for token creation from the verified user
        String userId = verfiedUser.getId()
                .toString();
        String email = verfiedUser.getEmail();
        Role role = verfiedUser.getRole();

        // generate tokens
        String accessToken = jwtService.generateAccessToken(userId, email, role.name());
        String refreshToken = jwtService.generateRefreshToken(userId, email, role.name());

        return new AuthTokens(accessToken, refreshToken);
    }

    private String extractCookieAndReturnToken(HttpServletRequest request) {
        // extract the refreshToken cookie from the request cookies and return the stored
        // refreshToken from it
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName()
                    .equals("refreshToken")) {
                // return early if the cookie has the name "refreshToken"
                return cookie.getValue();
            }
        }
        return null;
    }


}
