package com.porodkin.personalfinancetracker.controllers;

import com.porodkin.personalfinancetracker.dto.request.security.AuthenticationRequest;
import com.porodkin.personalfinancetracker.dto.request.security.RegisterNewUserRequest;
import com.porodkin.personalfinancetracker.dto.request.user.NewUser;
import com.porodkin.personalfinancetracker.dto.response.security.AuthenticationResponse;
import com.porodkin.personalfinancetracker.dto.response.user.NewUserResponse;
import com.porodkin.personalfinancetracker.service.security.AuthenticationService;
import com.porodkin.personalfinancetracker.service.user.UserRegistration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final AuthenticationService authenticationService;
    private final UserRegistration userRegistration;

    public UserController(AuthenticationService authenticationService, UserRegistration userRegistration) {
        this.authenticationService = authenticationService;
        this.userRegistration = userRegistration;
    }

    @PostMapping(
            value = "/authenticate",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<? extends AuthenticationResponse> authenticateUser(@RequestBody AuthenticationRequest request) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authenticationService.authenticate(request.email(), request.password()));
    }

    @PostMapping(
            value = "/register",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<? extends NewUserResponse> registerUser(@RequestBody RegisterNewUserRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userRegistration.register(new NewUser(request.username(), request.email(), request.password())));
    }
}


