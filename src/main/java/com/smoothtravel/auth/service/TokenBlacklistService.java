package com.smoothtravel.auth.service;

import com.smoothtravel.infrastructure.redis.RedisService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;

@ApplicationScoped
public class TokenBlacklistService {

    private static final String KEY_PREFIX = "blacklist:";

    @Inject
    RedisService redisService;

    public void blacklist(String jti, long expSeconds) {
        if (expSeconds > 0) {
            redisService.set(KEY_PREFIX + jti, "1", Duration.ofSeconds(expSeconds));
        }
    }

    public boolean isBlacklisted(String jti) {
        return redisService.get(KEY_PREFIX + jti) != null;
    }
}
