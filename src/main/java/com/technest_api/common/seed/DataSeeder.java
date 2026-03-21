package com.technest_api.common.seed;

import com.technest_api.module.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {
    private final UserService userService;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            userService.createAdminUserIfNotExists();
        };
    }

}
