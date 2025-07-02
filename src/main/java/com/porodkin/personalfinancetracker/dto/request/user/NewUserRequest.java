package com.porodkin.personalfinancetracker.dto.request.user;

public record NewUserRequest(String username, String email, String password) {
}
