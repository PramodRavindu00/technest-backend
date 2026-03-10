package com.technest_api.module.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank()
    private String userName;

    @NotBlank()
    @Email()
    private String email;

    @NotBlank()
    private String password;

}
