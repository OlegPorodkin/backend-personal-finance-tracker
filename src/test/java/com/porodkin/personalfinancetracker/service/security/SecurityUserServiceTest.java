package com.porodkin.personalfinancetracker.service.security;

import com.porodkin.personalfinancetracker.persistence.entity.FintrackerUser;
import com.porodkin.personalfinancetracker.persistence.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUserServiceTest {

    @Mock
    UserRepository repository;
    @InjectMocks
    SecurityUserService service;

    private static final String EMAIL = "admin@test.com";
    private static final String PWD   = "hashed-pwd";

    @Test
    @DisplayName("loadUserByUsername() return UserDetails on success")
    void loadSuccess() {
        FintrackerUser entity = new FintrackerUser();
        entity.setEmail(EMAIL);
        entity.setPassword(PWD);

        when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(entity));

        UserDetails details = service.loadUserByUsername(EMAIL);

        assertEquals(EMAIL, details.getUsername());
        assertEquals(PWD,   details.getPassword());

        List<String> roles = details.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertEquals(List.of("ROLE_ADMIN"), roles);

        verify(repository).findByEmail(EMAIL);
    }

    @Nested
    class UserNotFound {

        @Test
        @DisplayName("just throw UsernameNotFoundException and nothing else")
        void throwsWhenMissing() {
            when(repository.findByEmail(EMAIL)).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class,
                    () -> service.loadUserByUsername(EMAIL));

            verify(repository).findByEmail(EMAIL);
        }
    }
}