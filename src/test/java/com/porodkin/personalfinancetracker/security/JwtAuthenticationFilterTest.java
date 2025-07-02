package com.porodkin.personalfinancetracker.security;

import com.porodkin.personalfinancetracker.service.security.SecurityUserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private static final String TOKEN    = "dummy.jwt.token";
    private static final String BEARER   = "Bearer " + TOKEN;
    private static final String EMAIL    = "user@test.com";

    private JwtAuthTokenUtils   jwtUtils;
    private SecurityUserService userService;
    private JwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        jwtUtils    = mock(JwtAuthTokenUtils.class);
        userService = mock(SecurityUserService.class);

        filter   = new JwtAuthenticationFilter(jwtUtils, userService);
        request  = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        chain    = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void noHeader_passesThrough() throws Exception {
        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(200, response.getStatus());
    }

    @Test
    void validToken_setsSecurityContextAndContinues() throws Exception {
        request.addHeader("Authorization", BEARER);

        when(jwtUtils.extractEmail(TOKEN)).thenReturn(EMAIL);

        UserDetails ud = mock(UserDetails.class);
        when(ud.getUsername()).thenReturn(EMAIL);
        when(ud.getAuthorities()).thenReturn(Collections.EMPTY_LIST);
        when(userService.loadUserByUsername(EMAIL)).thenReturn(ud);

        when(jwtUtils.validateToken(TOKEN, ud)).thenReturn(true);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);

        var auth = (UsernamePasswordAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(auth);
        assertEquals(ud, auth.getPrincipal());
        assertInstanceOf(WebAuthenticationDetails.class, auth.getDetails());
        assertEquals(ud.getAuthorities(), auth.getAuthorities());

        InOrder inOrder = inOrder(jwtUtils, userService);
        inOrder.verify(jwtUtils).extractEmail(TOKEN);
        inOrder.verify(userService).loadUserByUsername(EMAIL);
        inOrder.verify(jwtUtils).validateToken(TOKEN, ud);
    }

    @Test
    void expiredToken_returns401AndStops() throws Exception {
        request.addHeader("Authorization", BEARER);
        when(jwtUtils.extractEmail(TOKEN))
                .thenThrow(new ExpiredJwtException(null, null, "expired"));

        filter.doFilterInternal(request, response, chain);

        verify(chain, never()).doFilter(any(), any());
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("ACCESS_TOKEN_EXPIRED"));
    }

    @Test
    void invalidToken_returns401AndStops() throws Exception {
        request.addHeader("Authorization", BEARER);
        when(jwtUtils.extractEmail(TOKEN)).thenThrow(new JwtException("bad"));

        filter.doFilterInternal(request, response, chain);

        verify(chain, never()).doFilter(any(), any());
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("INVALID_TOKEN"));
    }
}