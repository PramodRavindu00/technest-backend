package com.technest_api.module.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank()
    @Size(min = 3, max = 50)
    private String userName;

    @NotBlank()
    @Email()
    private String email;

    @NotBlank()
    @Size(min = 8)
    private String password;
}
