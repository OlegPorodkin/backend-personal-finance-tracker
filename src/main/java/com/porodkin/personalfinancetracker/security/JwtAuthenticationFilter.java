package com.porodkin.personalfinancetracker.security;

import com.porodkin.personalfinancetracker.service.security.SecurityUserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";

    private final JwtAuthTokenUtils jwtUtils;
    private final SecurityUserService userService;

    public JwtAuthenticationFilter(JwtAuthTokenUtils jwtUtils, SecurityUserService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader(HEADER_STRING);
        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            jwt = authorizationHeader.substring(7);
            try {
                email = jwtUtils.extractEmail(jwt);
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().println("{\"error\":\"ACCESS_TOKEN_EXPIRED\"}");
                return;
            }catch (JwtException ex) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"INVALID_TOKEN\"}");
                return;
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(email);
            if (jwtUtils.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken  authenticationUser = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationUser.setDetails(new WebAuthenticationDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationUser);
            }
        }

        filterChain.doFilter(request, response);
    }
}
