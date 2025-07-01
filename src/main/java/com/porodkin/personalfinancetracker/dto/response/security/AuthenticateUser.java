package com.porodkin.personalfinancetracker.dto.response.security;

public class AuthenticateUser extends AuthenticationResponse {
    private final String authToken;
    private final String issuedAt;
    private final String expiration;

    public AuthenticateUser(String authToken, String issuedAt, String expiration) {
        this.authToken = authToken;
        this.issuedAt = issuedAt;
        this.expiration = expiration;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getIssuedAt() {
        return issuedAt;
    }

    public String getExpiration() {
        return expiration;
    }
}
