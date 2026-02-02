package com.smoothtravel.auth.service;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class TokenService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    public String generateToken(UUID userId, String email) {
        return Jwt.issuer(issuer)
                .claim("jti", UUID.randomUUID().toString())
                .subject(userId.toString())
                .upn(email)
                .groups(Set.of("user"))
                .expiresIn(Duration.ofHours(24))
                .sign();
    }
}
