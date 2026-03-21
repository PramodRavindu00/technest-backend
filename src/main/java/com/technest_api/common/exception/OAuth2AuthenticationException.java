package com.technest_api.common.exception;

import lombok.Getter;

@Getter
public class OAuth2AuthenticationException extends RuntimeException {
    private final String errorCode;

    public OAuth2AuthenticationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public static OAuth2AuthenticationException principalIsNull() {
        return new OAuth2AuthenticationException("OAuth2 principal is null", "server_error");
    }

    public static OAuth2AuthenticationException missingAttributes(String googleId, String email) {
        return new OAuth2AuthenticationException(
                "Missing required OAuth2 attributes. googleId=" + googleId + ", email=" + email,
                "server_error");
    }

    public static OAuth2AuthenticationException accountConflict(String email) {
        return new OAuth2AuthenticationException("An account already exists with email: " + email,
                "account_conflict");
    }

}
