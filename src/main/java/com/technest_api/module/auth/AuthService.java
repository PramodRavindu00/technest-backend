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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public void localSignUp(SignUpRequest dto) {
        Optional<User> existByEmail = userRepo.findByEmail(dto.getEmail());
        if (existByEmail.isPresent()) {
            User userByEmail = existByEmail.get();
            if (userByEmail.getGoogleId() != null && userByEmail.getPasswordHash() == null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Email already exists with" + " a google login");
            }
            if (userByEmail.getLinkedInId() != null && userByEmail.getPasswordHash() == null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Email already exists with" + " a linkedin login");
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User newUser = User.builder()
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .build();
        userRepo.save(newUser);
    }


    public AuthResponse localLogin(LoginRequest dto) {
        return null;
    }

    public AuthResponse handleOAuthLogin() {
        return null;
    }
}
