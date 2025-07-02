package com.porodkin.personalfinancetracker.configuration;

import com.porodkin.personalfinancetracker.persistence.repository.UserRepository;
import com.porodkin.personalfinancetracker.service.user.UserRegistration;
import com.porodkin.personalfinancetracker.service.user.impl.NewUserRegistration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserConfiguration {

    @Bean
    public UserRegistration userRegistration(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new NewUserRegistration(userRepository, passwordEncoder);
    }
}
