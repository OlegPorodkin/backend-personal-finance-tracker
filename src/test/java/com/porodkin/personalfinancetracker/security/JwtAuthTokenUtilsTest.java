package com.porodkin.personalfinancetracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthTokenUtilsTest {
    private static final String SECRET = "my-test-super-secret-key-32-bytes-long";
    private static final long EXPIRATION_MILLIS = 3_600_000;

    private JwtAuthTokenUtils utils;

    @BeforeEach
    void setUp() {
        utils = new JwtAuthTokenUtils(SECRET, EXPIRATION_MILLIS);
    }

    private Claims parse(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    @Test
    @DisplayName("generateToken(email, issuedAt, exp) puts correct claims")
    void testGenerateTokenWithExplicitDates() {
        String username = "test@test.com";
        long currentTimeMillis = System.currentTimeMillis();
        Date issuedAt = new Date(currentTimeMillis);
        Date expiration = new Date(currentTimeMillis + 10_000);

        String token = utils.generateToken(username, issuedAt, expiration);
        Claims c = parse(token);

        assertEquals(username, c.getSubject());
        assertEquals(issuedAt.toString(), c.getIssuedAt().toString());
        assertEquals(expiration.toString(), c.getExpiration().toString());
    }

    @Test
    @DisplayName("generateToken(email) uses now() and props expiry correctly")
    void testGenerateTokenDefault() {
        String username = "test@test.com";

        String token = utils.generateToken(username);
        Claims c = parse(token);

        assertEquals(username, c.getSubject());
        long delta = c.getExpiration().getTime() - c.getIssuedAt().getTime();
        assertTrue(delta >= EXPIRATION_MILLIS - 50 && delta <= EXPIRATION_MILLIS + 50);
    }

    @Test
    void testExtractUsernameIssuedAtExpiration() {
        String token = utils.generateToken("test@test.com");
        assertEquals("test@test.com", utils.extractEmail(token));
        assertNotNull(utils.extractIssuedAt(token));
        assertNotNull(utils.extractExpiration(token));
    }

    @Test
    void testIsTokenExpiredFalse() {
        String token = utils.generateToken("test@test.com");
        assertFalse(utils.isTokenExpired(token));
    }

    @Test
    void testIsTokenExpiredTrue() {
        long currentTimeMillis = System.currentTimeMillis();
        Date issuedAt = new Date(currentTimeMillis + 3_600_000);
        Date expiration = new Date(currentTimeMillis + 2000);

        String token = utils.generateToken(
                "test@test.com",
                issuedAt,
                expiration);
        assertTrue(utils.isTokenExpired(token));
    }

    @Nested
    class ValidateToken {

        @Test
        @DisplayName("valid token & matching user ⇒ true")
        void success() {
            String username = "test@test.com";
            String token = utils.generateToken(username);

            UserDetails ud = mock(UserDetails.class);
            when(ud.getUsername()).thenReturn(username);

            assertTrue(utils.validateToken(token, ud));
            verify(ud).getUsername();
        }

        @Test
        @DisplayName("token valid but username mismatch ⇒ false")
        void wrongUser() {
            String token = utils.generateToken("test@test.com");

            UserDetails ud = mock(UserDetails.class);
            when(ud.getUsername()).thenReturn("test_2@test.com");

            assertFalse(utils.validateToken(token, ud));
        }

        @Test
        @DisplayName("token expired ⇒ false")
        void expired() {
            String token = utils.generateToken(
                    "test@test.com",
                    new Date(System.currentTimeMillis() - 10_000),
                    new Date(System.currentTimeMillis() - 1_000));

            UserDetails ud = mock(UserDetails.class);
            when(ud.getUsername()).thenReturn("test@test.com");

            assertFalse(utils.validateToken(token, ud));
        }

        @Test
        @DisplayName("malformed / unsigned token ⇒ false (exception branch)")
        void invalidSignatureOrMalformed() {
            String bogus = "foo.bar.baz";

            UserDetails ud = mock(UserDetails.class);
            when(ud.getUsername()).thenReturn("test@test.com");

            assertFalse(utils.validateToken(bogus, ud));
        }
    }
}