package com.smoothtravel.auth.service;

import com.smoothtravel.infrastructure.redis.RedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock
    RedisService redisService;

    @InjectMocks
    TokenBlacklistService tokenBlacklistService;

    private final String testJti = "test-jti-12345";

    @Test
    void shouldBlacklistTokenWithPositiveExpiry() {
        long expSeconds = 3600; // 1 hour
        doNothing().when(redisService).set("blacklist:" + testJti, "1", Duration.ofSeconds(expSeconds));

        tokenBlacklistService.blacklist(testJti, expSeconds);

        verify(redisService).set("blacklist:" + testJti, "1", Duration.ofSeconds(expSeconds));
    }

    @Test
    void shouldNotBlacklistTokenWithZeroExpiry() {
        long expSeconds = 0;

        tokenBlacklistService.blacklist(testJti, expSeconds);

        verify(redisService, never()).set("blacklist:" + testJti, "1", Duration.ofSeconds(expSeconds));
    }

    @Test
    void shouldNotBlacklistTokenWithNegativeExpiry() {
        long expSeconds = -100;

        tokenBlacklistService.blacklist(testJti, expSeconds);

        verify(redisService, never()).set("blacklist:" + testJti, "1", Duration.ofSeconds(expSeconds));
    }

    @Test
    void shouldReturnTrueWhenTokenIsBlacklisted() {
        when(redisService.get("blacklist:" + testJti)).thenReturn("1");

        boolean result = tokenBlacklistService.isBlacklisted(testJti);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenTokenIsNotBlacklisted() {
        when(redisService.get("blacklist:" + testJti)).thenReturn(null);

        boolean result = tokenBlacklistService.isBlacklisted(testJti);

        assertFalse(result);
    }
}
