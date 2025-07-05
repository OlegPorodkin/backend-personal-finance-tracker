package com.porodkin.personalfinancetracker.controllers.exceptions.handler.user;

import com.porodkin.personalfinancetracker.dto.response.security.AuthenticationResponse;
import com.porodkin.personalfinancetracker.dto.response.security.ExpiredJwtResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExpiredJwtExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<? extends AuthenticationResponse> handleExpiredJwtException() {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ExpiredJwtResponse("ACCESS_TOKEN_EXPIRED"));
    }
}
