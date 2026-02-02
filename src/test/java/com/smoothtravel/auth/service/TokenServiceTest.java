package com.smoothtravel.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    TokenService tokenService;

    @BeforeEach
    void setUp() throws Exception {
        tokenService = new TokenService();
        Field issuerField = TokenService.class.getDeclaredField("issuer");
        issuerField.setAccessible(true);
        issuerField.set(tokenService, "smoothtravel");
    }

    @Test
    void shouldGenerateToken() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";

        String token = tokenService.generateToken(userId, email);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }
}
