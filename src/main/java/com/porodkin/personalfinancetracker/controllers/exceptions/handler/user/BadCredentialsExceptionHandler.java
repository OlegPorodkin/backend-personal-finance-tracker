package com.porodkin.personalfinancetracker.controllers.exceptions.handler.user;

import com.porodkin.personalfinancetracker.dto.response.security.AuthenticationResponse;
import com.porodkin.personalfinancetracker.dto.response.security.BadCredentialsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BadCredentialsExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<? extends AuthenticationResponse> handleDisabledException() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new BadCredentialsResponse(
                        "User has a bad credentials."));
    }
}
