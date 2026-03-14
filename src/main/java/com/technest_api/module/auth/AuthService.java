package com.technest_api.module.auth;

import com.technest_api.module.auth.dto.LoginRequest;
import com.technest_api.module.auth.dto.SignUpRequest;
import com.technest_api.module.user.UserRepository;
import com.technest_api.module.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
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
            if (userByEmail.getPasswordHash() == null) {
                String connectedOauthProviders = getConnectedOauthProviders(userByEmail);
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Email already exists with" + connectedOauthProviders + " login");
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User newUser = User.builder()
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .build();
        userRepo.save(newUser);
    }


    public void localLogin(LoginRequest dto) {
        Optional<User> existingUserByEmail = userRepo.findByEmail(dto.getEmail());
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
    }

    public void oAuthLogin() {

    }

    private String getConnectedOauthProviders(User user) {
        List<String> oauthProviders = new ArrayList<>();
        if (user.getGoogleId() != null) oauthProviders.add("Google");
        if (user.getLinkedInId() != null) oauthProviders.add("LinkedIn");
        return oauthProviders.isEmpty() ? "an external provider" :
                String.join(" and ", oauthProviders);
    }
}
