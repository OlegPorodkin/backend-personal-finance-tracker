package com.porodkin.personalfinancetracker.service.user.impl;

import com.porodkin.personalfinancetracker.controllers.exceptions.UserAlreadyExistException;
import com.porodkin.personalfinancetracker.dto.request.user.NewUser;
import com.porodkin.personalfinancetracker.dto.response.user.CreatedUserResponse;
import com.porodkin.personalfinancetracker.persistence.entity.FintrackerUser;
import com.porodkin.personalfinancetracker.persistence.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewUserRegistrationTest {

    @Mock
    UserRepository repository;
    @Mock
    PasswordEncoder encoder;

    @InjectMocks
    NewUserRegistration registration;

    private static final String USERNAME = "new_user";
    private static final String EMAIL = "new@test.com";
    private static final String RAW   = "plain-pwd";
    private static final String HASH  = "$2a$10$hashValue";

    @Test
    @DisplayName("register() hashes password and saved user")
    void registerSuccess() {
        when(repository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        when(encoder.encode(RAW)).thenReturn(HASH);

        CreatedUserResponse dto = registration.register(new NewUser(USERNAME, EMAIL, RAW));
        assertNotNull(dto);

        ArgumentCaptor<FintrackerUser> cap = ArgumentCaptor.forClass(FintrackerUser.class);
        verify(repository).save(cap.capture());

        FintrackerUser saved = cap.getValue();
        assertEquals(EMAIL, saved.getEmail());
        assertEquals(HASH,  saved.getPassword());
        assertTrue(saved.isEnabled());

        verify(encoder).encode(RAW);
    }

    @Nested
    class DuplicateEmail {

        @Test
        @DisplayName("if email exist → UserAlreadyExistException and don`t have any encode/save")
        void duplicateThrows() {
            when(repository.findByEmail(EMAIL))
                    .thenReturn(Optional.of(new FintrackerUser()));

            assertThrows(UserAlreadyExistException.class,
                    () -> registration.register(new NewUser(USERNAME, EMAIL, RAW)));

            verify(repository, never()).save(any());
            verify(encoder,    never()).encode(anyString());
        }
    }
}