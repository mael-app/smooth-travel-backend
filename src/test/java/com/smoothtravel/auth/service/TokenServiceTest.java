package com.smoothtravel.auth.service;

import io.smallrye.jwt.build.Jwt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    TokenService tokenService;

    @BeforeAll
    static void generateKeys() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(kp.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----";

        System.clearProperty("smallrye.jwt.sign.key.location");
        System.setProperty("smallrye.jwt.sign.key", privateKeyPem);
    }

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
