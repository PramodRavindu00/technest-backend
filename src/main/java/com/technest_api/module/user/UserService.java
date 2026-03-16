package com.technest_api.module.user;

import com.technest_api.module.user.dto.UserResponseDto;
import com.technest_api.module.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserResponseDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toUserResponseDto)
                .toList();
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
