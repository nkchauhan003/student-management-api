package com.cb.unit.security;

import com.cb.security.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    void generateToken_and_extractUsername_shouldWork() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        String extracted = jwtUtil.extractUsername(token);
        assertEquals(username, extracted);
    }

    @Test
    void validateToken_shouldReturnTrue_forValidToken() {
        String username = "validuser";
        String token = jwtUtil.generateToken(username);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);

        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void validateToken_shouldReturnFalse_forWrongUser() {
        String token = jwtUtil.generateToken("userA");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("userB");

        assertFalse(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void extractUsername_shouldThrow_forExpiredToken() throws InterruptedException {
        // Create a JwtUtil with a short expiration for this test
        JwtUtil shortLivedJwtUtil = new JwtUtil() {
            @Override
            public String generateToken(String username) {
                return io.jsonwebtoken.Jwts.builder()
                        .setSubject(username)
                        .setIssuedAt(new java.util.Date())
                        .setExpiration(new java.util.Date(System.currentTimeMillis() + 500)) // 0.5 sec
                        .signWith(this.key, io.jsonwebtoken.SignatureAlgorithm.HS256)
                        .compact();
            }
        };
        String token = shortLivedJwtUtil.generateToken("expiringuser");
        TimeUnit.MILLISECONDS.sleep(600); // Wait for token to expire

        assertThrows(ExpiredJwtException.class, () -> shortLivedJwtUtil.extractUsername(token));
    }
}