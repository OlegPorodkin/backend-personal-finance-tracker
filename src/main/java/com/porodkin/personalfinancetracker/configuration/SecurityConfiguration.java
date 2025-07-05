package com.porodkin.personalfinancetracker.configuration;

import com.porodkin.personalfinancetracker.persistence.repository.UserRepository;
import com.porodkin.personalfinancetracker.security.JwtAuthTokenUtils;
import com.porodkin.personalfinancetracker.security.JwtAuthenticationFilter;
import com.porodkin.personalfinancetracker.service.security.SecurityUserService;
import com.porodkin.personalfinancetracker.service.security.AuthenticationService;
import com.porodkin.personalfinancetracker.service.security.impl.JwtAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("${jwt.secret}")
    public String secret;
    @Value("${jwt.expiration}")
    public Long expiration;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        final String[] approvalPath = { "/api/v1/users/authenticate", "api/v1/users/register" };

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(approvalPath).permitAll()
                                .anyRequest().authenticated()
                )

                .sessionManagement(
                        session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtAuthTokenUtils jwtUtils, SecurityUserService userService){
        return new JwtAuthenticationFilter(jwtUtils, userService);
    }

    @Bean
    public JwtAuthTokenUtils jwtUtils() {
        return new JwtAuthTokenUtils(secret, expiration);
    }

    @Bean
    public SecurityUserService securityUserService(UserRepository userRepository) {
        return new SecurityUserService(userRepository);
    }

    @Bean
    public AuthenticationService authenticationService(
            SecurityUserService userService,
            AuthenticationManager authenticationManager,
            JwtAuthTokenUtils jwtUtils
    ) {
        return new JwtAuthService(userService, authenticationManager,  jwtUtils);
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("ADMIN").implies("USER")
                .build();
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }
}
