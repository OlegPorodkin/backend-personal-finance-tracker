package com.porodkin.personalfinancetracker.service.security.impl;

import com.porodkin.personalfinancetracker.dto.response.security.AuthenticateUser;
import com.porodkin.personalfinancetracker.security.JwtAuthTokenUtils;
import com.porodkin.personalfinancetracker.service.security.SecurityUserService;
import com.porodkin.personalfinancetracker.service.security.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtAuthService implements AuthenticationService {

    private final SecurityUserService securityUserService;
    private final AuthenticationManager authenticationManager;
    private final JwtAuthTokenUtils jwtAuthTokenUtils;

    public JwtAuthService(SecurityUserService securityUserService, AuthenticationManager authenticationManager, JwtAuthTokenUtils jwtAuthTokenUtils) {
        this.securityUserService = securityUserService;
        this.authenticationManager = authenticationManager;
        this.jwtAuthTokenUtils = jwtAuthTokenUtils;
    }

    @Override
    public AuthenticateUser authenticate(String email, String password) throws DisabledException, BadCredentialsException {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        final UserDetails userDetails = securityUserService
                .loadUserByUsername(email);

        final String jwt = jwtAuthTokenUtils.generateToken(userDetails.getUsername());

        return new AuthenticateUser(jwt, jwtAuthTokenUtils.extractIssuedAt(jwt).toString(), jwtAuthTokenUtils.extractExpiration(jwt).toString());
    }
}
