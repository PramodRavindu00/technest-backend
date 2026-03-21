package com.technest_api.module.user;

import com.technest_api.common.constant.enums.Role;
import com.technest_api.module.user.dto.CreateUserDto;
import com.technest_api.module.user.dto.UserResponseDto;
import com.technest_api.module.user.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
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
