package com.technest_api.module.auth;

import com.technest_api.common.annotation.SetRefreshTokenCookie;
import com.technest_api.module.auth.dto.AuthTokens;
import com.technest_api.module.auth.dto.LoginRequest;
import com.technest_api.module.auth.dto.SignUpRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void localSignUp(@Valid @RequestBody SignUpRequest dto) {
        authService.localSignUp(dto);
    }

    @PostMapping("/login")
    @SetRefreshTokenCookie
    public ResponseEntity<AuthTokens> localLogin(@Valid @RequestBody LoginRequest dto) {
        return ResponseEntity.ok(authService.localLogin(dto));
    }

    @PostMapping("/refresh")
    @SetRefreshTokenCookie
    public ResponseEntity<AuthTokens> refresh(HttpServletRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    // should implement
//    @PostMapping("/exchange")
//    public ResponseEntity<AuthTokens> exchange(@Valid @RequestBody AuthCodeExchangeRequest dto) {
//        return ResponseEntity.ok(authService.exchange(dto));
//    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        return ResponseEntity.ok()
                .build();
    }


}
