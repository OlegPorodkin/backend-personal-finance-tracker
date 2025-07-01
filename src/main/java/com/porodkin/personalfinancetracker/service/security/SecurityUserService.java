package com.porodkin.personalfinancetracker.service.security;

import com.porodkin.personalfinancetracker.persistence.entity.FintrackerUser;
import com.porodkin.personalfinancetracker.persistence.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class SecurityUserService implements UserDetailsService {

    private final UserRepository userRepository;

    public SecurityUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        FintrackerUser user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));


        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_ADMIN")
                .build();
    }
}
