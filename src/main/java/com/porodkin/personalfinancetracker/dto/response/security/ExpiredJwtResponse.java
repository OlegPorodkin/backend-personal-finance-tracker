package com.porodkin.personalfinancetracker.dto.response.security;

import org.springframework.http.HttpStatus;

public class ExpiredJwtResponse extends AuthenticationResponse {
    private final String massage;
    private final Integer status;
    private final String reason;


    public ExpiredJwtResponse(String massage) {
        this.massage = massage;
        this.status = HttpStatus.UNAUTHORIZED.value();
        this.reason = "Token is expired";
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }
}
