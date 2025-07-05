package com.porodkin.personalfinancetracker.controllers.exceptions.handler.user;

import com.porodkin.personalfinancetracker.dto.response.security.AuthenticationResponse;
import com.porodkin.personalfinancetracker.dto.response.security.DisableResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserDisableExceptionHandler {

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<? extends AuthenticationResponse> handleDisabledException() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new DisableResponse("User is disable."));
    }
}
