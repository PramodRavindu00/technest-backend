package com.technest_api.module.user.dto;

import com.technest_api.module.auth.dto.SignUpRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserDto {

    @NotBlank()
    @Email()
    private String email;

    @NotBlank()
    @Size(min = 8)
    private String password;

    public CreateUserDto(SignUpRequest dto) {
        this.email = dto.getEmail();
        this.password = dto.getPassword();
    }
}
