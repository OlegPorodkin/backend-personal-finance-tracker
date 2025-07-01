package com.porodkin.personalfinancetracker.service.security;

import com.porodkin.personalfinancetracker.dto.response.security.AuthenticateUser;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;

public interface AuthenticationService {

    AuthenticateUser authenticate(String email, String password) throws DisabledException, BadCredentialsException;
}
