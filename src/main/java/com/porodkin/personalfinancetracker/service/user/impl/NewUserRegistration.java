package com.porodkin.personalfinancetracker.service.user.impl;

import com.porodkin.personalfinancetracker.dto.request.user.NewUserRequest;
import com.porodkin.personalfinancetracker.dto.response.user.CreatedUserResponse;
import com.porodkin.personalfinancetracker.persistence.entity.FintrackerUser;
import com.porodkin.personalfinancetracker.persistence.repository.UserRepository;
import com.porodkin.personalfinancetracker.service.user.UserRegistration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class NewUserRegistration implements UserRegistration {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public NewUserRegistration(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.repository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CreatedUserResponse register(NewUserRequest user) throws BadCredentialsException {
        repository
                .findByEmail(user.email())
                .ifPresent(fintrackerUser -> {throw new BadCredentialsException("User is already registered");});

        FintrackerUser fintrackerUser = new FintrackerUser();
        fintrackerUser.setEmail(user.email());
        fintrackerUser.setPassword(passwordEncoder.encode(user.password()));
        fintrackerUser.setEnabled(true);

        repository.save(fintrackerUser);

        return new CreatedUserResponse();
    }
}
