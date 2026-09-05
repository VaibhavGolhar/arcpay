package com.btech_major_project.Personal_Cloud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // Set a valid secret key for HMAC SHA256 (at least 32 bytes)
        jwtService = new JwtService("my-super-secret-key-that-is-at-least-32-bytes-long", 3600000L);
    }

    @Test
    void testGenerateAndValidateToken() {
        String email = "test@example.com";
        String token = jwtService.generateToken(email);
        
        assertNotNull(token);
        assertTrue(jwtService.isValid(token));
        assertEquals(email, jwtService.extractSubject(token));
    }

    @Test
    void testValidateInvalidToken() {
        assertFalse(jwtService.isValid("invalid.token.here"));
    }
}
