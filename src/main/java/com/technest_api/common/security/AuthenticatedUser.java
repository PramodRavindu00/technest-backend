package com.technest_api.common.security;

import com.technest_api.common.constant.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthenticatedUser {
    private String id;
    private String email;
    private Role role;
}
