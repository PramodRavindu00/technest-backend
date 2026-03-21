package com.technest_api.module.user;

import com.technest_api.common.constant.enums.Role;
import com.technest_api.common.exception.OAuth2AuthenticationException;
import com.technest_api.module.user.dto.CreateUserDto;
import com.technest_api.module.user.dto.UserResponseDto;
import com.technest_api.module.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDto> getAll() {
        return userRepo.findAll()
                .stream()
                .map(this::toUserResponseDto)
                .toList();
    }

    public void createAdminUserIfNotExists() {
        if (userRepo.existsByEmail("admin@technest.com")) {
            log.info("Admin already exists - skipping seed");
            return;
        }

        User admin = User.builder()
                .email("admin@technest.com")
                .passwordHash(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .emailVerified(true)
                .build();
        userRepo.save(admin);
        log.info("Admin user seeded successfully");
    }

    public void createUser(CreateUserDto dto) {
        User newUser = User.builder()
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .build();
        userRepo.save(newUser);
    }

    public Optional<User> findById(String id) {
        return userRepo.findById(UUID.fromString(id));
    }

    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    // this method used in Oauth2 authentication
    public User findOrCreate(String googleId, String email) {
        Optional<User> byGoogleId = userRepo.findByGoogleId(googleId);
        if (byGoogleId.isPresent()) {
            return byGoogleId.get();  // return the user found from googleId
        }
        //not found by googleId? check email exists
        Optional<User> byEmail = userRepo.findByEmail(email);
        if (byEmail.isPresent()) {
            throw OAuth2AuthenticationException.accountConflict(email);
        }

        User newUser = User.builder()
                .email(email)
                .googleId(googleId)
                .build();
        return userRepo.save(newUser);
    }

    private UserResponseDto toUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .googleId(user.getGoogleId())
                .linkedinId(user.getLinkedInId())
                .role(user.getRole())
                .build();
    }
}
