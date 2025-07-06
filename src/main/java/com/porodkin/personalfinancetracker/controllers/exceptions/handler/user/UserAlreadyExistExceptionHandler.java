package com.porodkin.personalfinancetracker.controllers.exceptions.handler.user;

import com.porodkin.personalfinancetracker.controllers.exceptions.UserAlreadyExistException;
import com.porodkin.personalfinancetracker.dto.response.security.AuthenticationResponse;
import com.porodkin.personalfinancetracker.dto.response.user.UserAlreadyExist;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserAlreadyExistExceptionHandler {

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<? extends AuthenticationResponse> handleUserAlreadyExistException() throws UserAlreadyExistException {
        return  ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new UserAlreadyExist(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Email is already busy."));
    }
}
