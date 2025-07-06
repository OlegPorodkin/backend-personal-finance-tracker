package com.porodkin.personalfinancetracker.dto.response.user;

import com.porodkin.personalfinancetracker.dto.response.security.AuthenticationResponse;

public class UserAlreadyExist extends AuthenticationResponse {
    private final Integer code;
    private final String massage;

    public UserAlreadyExist(Integer code, String massage) {
        this.code = code;
        this.massage = massage;
    }

    public Integer getCode() {
        return code;
    }

    public String getMassage() {
        return massage;
    }
}
