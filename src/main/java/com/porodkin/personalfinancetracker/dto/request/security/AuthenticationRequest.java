package com.porodkin.personalfinancetracker.dto.request.security;

public record AuthenticationRequest(String email, String password) {
}
