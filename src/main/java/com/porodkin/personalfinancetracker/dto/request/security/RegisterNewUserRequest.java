package com.porodkin.personalfinancetracker.dto.request.security;

public record RegisterNewUserRequest(String username, String email, String password) {
}
