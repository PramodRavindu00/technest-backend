package com.technest_api.module.auth;

import com.technest_api.module.auth.dto.AuthResponse;
import com.technest_api.module.auth.dto.LoginRequest;
import com.technest_api.module.auth.dto.SignUpRequest;
import com.technest_api.module.user.UserRepository;
import com.technest_api.module.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public void localSignUp(SignUpRequest dto) {
        Boolean existByUserName = userRepo.existsByUserName(dto.getUserName());
        if (existByUserName) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User name already exists");
        }
        Boolean existByEmail = userRepo.existsByEmail(dto.getEmail());
        if (existByEmail) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User newUser = User.builder()
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .build();
        userRepo.save(newUser);
    }


    public AuthResponse localLogin(LoginRequest dto) {
//        return {accessToken:""}
        return null;
    }
}
