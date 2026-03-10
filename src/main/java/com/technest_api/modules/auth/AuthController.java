package com.technest_api.modules.auth;

import com.technest_api.modules.auth.dto.LoginRequest;
import com.technest_api.modules.auth.dto.SignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("signup")
    public ResponseEntity<Void> localSignUp(@Valid @RequestBody SignUpRequest dto){
        authService.localSignUp(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("login")
    public ResponseEntity<Void> localLogin(@Valid @RequestBody LoginRequest dto){
        authService.localLogin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
