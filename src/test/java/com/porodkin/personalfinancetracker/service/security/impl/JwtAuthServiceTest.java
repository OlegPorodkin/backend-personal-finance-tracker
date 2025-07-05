package com.porodkin.personalfinancetracker.service.security.impl;

import com.porodkin.personalfinancetracker.dto.response.security.AuthenticateUser;
import com.porodkin.personalfinancetracker.security.JwtAuthTokenUtils;
import com.porodkin.personalfinancetracker.service.security.SecurityUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthServiceTest {

    private SecurityUserService securityUserService;
    private AuthenticationManager authenticationManager;
    private JwtAuthTokenUtils jwtAuthTokenUtils;

    private JwtAuthService authService;

    @BeforeEach
    void setUp() {
        securityUserService   = mock(SecurityUserService.class);
        authenticationManager = mock(AuthenticationManager.class);
        jwtAuthTokenUtils     = mock(JwtAuthTokenUtils.class);

        authService = new JwtAuthService(
                securityUserService,
                authenticationManager,
                jwtAuthTokenUtils);
    }

    @Test
    @DisplayName("authenticate() returns populated AuthenticateUser when credentials are valid")
    void authenticateSuccess() {
        String email    = "test@test.com";
        String password = "secret-pwd";

        UserDetails ud = mock(UserDetails.class);
        when(ud.getUsername()).thenReturn(email);
        when(securityUserService.loadUserByUsername(email)).thenReturn(ud);

        String token = "signed.jwt.token";
        Date issued  = new Date(1_000L);
        Date exp     = new Date(2_000L);

        when(jwtAuthTokenUtils.generateToken(email)).thenReturn(token);
        when(jwtAuthTokenUtils.extractIssuedAt(token)).thenReturn(issued);
        when(jwtAuthTokenUtils.extractExpiration(token)).thenReturn(exp);

        AuthenticateUser result = authService.authenticate(email, password);

        verify(authenticationManager).authenticate(argThat(matches(email, password)));
        verify(securityUserService).loadUserByUsername(email);
        verify(jwtAuthTokenUtils).generateToken(email);
        verify(jwtAuthTokenUtils).extractIssuedAt(token);
        verify(jwtAuthTokenUtils).extractExpiration(token);

        assertEquals(token,               result.getAuthToken());
        assertEquals(issued.toString(),   result.getIssuedAt());
        assertEquals(exp.toString(),      result.getExpiration());
    }

    private static ArgumentMatcher<UsernamePasswordAuthenticationToken> matches(String expectedUser, String expectedPwd) {
        return auth -> expectedUser.equals(auth.getPrincipal())
                && expectedPwd.equals(auth.getCredentials());
    }

    @Nested
    class ExceptionalFlows {

        @Test
        void disabledUser() {
            doThrow(new DisabledException("disabled"))
                    .when(authenticationManager).authenticate(any());

            assertThrows(DisabledException.class,
                    () -> authService.authenticate("test@test.com", "pwd"));

            verify(authenticationManager).authenticate(any());
            verifyNoInteractions(securityUserService, jwtAuthTokenUtils);
        }

        @Test
        void badCredentials() {
            doThrow(new BadCredentialsException("bad creds"))
                    .when(authenticationManager).authenticate(any());

            assertThrows(BadCredentialsException.class,
                    () -> authService.authenticate("test@test.com", "pwd"));

            verify(authenticationManager).authenticate(any());
            verifyNoInteractions(securityUserService, jwtAuthTokenUtils);
        }
    }
}