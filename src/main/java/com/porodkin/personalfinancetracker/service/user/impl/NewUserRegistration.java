package com.porodkin.personalfinancetracker.service.user.impl;

import com.porodkin.personalfinancetracker.controllers.exceptions.UserAlreadyExistException;
import com.porodkin.personalfinancetracker.dto.request.user.NewUser;
import com.porodkin.personalfinancetracker.dto.response.user.CreatedUserResponse;
import com.porodkin.personalfinancetracker.persistence.entity.FintrackerUser;
import com.porodkin.personalfinancetracker.persistence.entity.UserRole;
import com.porodkin.personalfinancetracker.persistence.repository.UserRepository;
import com.porodkin.personalfinancetracker.service.user.UserRegistration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Set;

public class NewUserRegistration implements UserRegistration {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public NewUserRegistration(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.repository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CreatedUserResponse register(NewUser user) throws UserAlreadyExistException {
        repository
                .findByEmail(user.email())
                .ifPresent(fintrackerUser -> {
                    throw new UserAlreadyExistException("User " + user.email() + " is already registered");
                });

        FintrackerUser fintrackerUser = new FintrackerUser();
        fintrackerUser.setEmail(user.email());
        fintrackerUser.setPassword(passwordEncoder.encode(user.password()));
        fintrackerUser.setEnabled(true);
        fintrackerUser.setRoles(Set.of(UserRole.USER));

        repository.save(fintrackerUser);

        return new CreatedUserResponse(HttpStatus.CREATED.value(), "User " + user.email() + " was created");
    }
}
