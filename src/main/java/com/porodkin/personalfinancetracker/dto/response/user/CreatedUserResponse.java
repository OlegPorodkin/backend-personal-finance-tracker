package com.porodkin.personalfinancetracker.dto.response.user;

public class CreatedUserResponse extends NewUserResponse {
    private final Integer code;
    private final String message;

    public CreatedUserResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
