package com.porodkin.personalfinancetracker.service.user;

import com.porodkin.personalfinancetracker.dto.request.user.NewUser;
import com.porodkin.personalfinancetracker.dto.response.user.CreatedUserResponse;
import org.springframework.security.authentication.BadCredentialsException;

public interface UserRegistration {

    CreatedUserResponse register(NewUser user) throws BadCredentialsException;
}
