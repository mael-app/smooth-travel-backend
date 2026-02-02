package com.smoothtravel.user.service;

import com.smoothtravel.infrastructure.mail.EmailService;
import com.smoothtravel.infrastructure.redis.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationCodeServiceTest {

    @Mock
    RedisService redisService;

    @Mock
    EmailService emailService;

    @InjectMocks
    VerificationCodeService verificationCodeService;

    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        // Reset mock behavior before each test
    }

    @Test
    void shouldGenerateAndSendCode() {
        doNothing().when(redisService).set(anyString(), anyString(), eq(Duration.ofMinutes(15)));
        doNothing().when(emailService).sendHtml(eq(testEmail), anyString(), anyString());

        verificationCodeService.generateAndSend(testEmail);

        verify(redisService).set(eq("verification:" + testEmail), anyString(), eq(Duration.ofMinutes(15)));
        verify(emailService).sendHtml(eq(testEmail), eq("SmoothTravel - Verification code"), anyString());
    }

    @Test
    void shouldReturnTrueWhenCodeExists() {
        when(redisService.get("verification:" + testEmail)).thenReturn("ABC123:1234567890123");

        boolean result = verificationCodeService.hasPendingCode(testEmail);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenNoCodeExists() {
        when(redisService.get("verification:" + testEmail)).thenReturn(null);

        boolean result = verificationCodeService.hasPendingCode(testEmail);

        assertFalse(result);
    }

    @Test
    void shouldAllowResendWhenNoCodeExists() {
        when(redisService.get("verification:" + testEmail)).thenReturn(null);

        boolean result = verificationCodeService.canResend(testEmail);

        assertTrue(result);
    }

    @Test
    void shouldAllowResendAfterCooldownPeriod() {
        // Create a timestamp that is older than the 30-second cooldown
        long oldTimestamp = Instant.now().minusSeconds(60).toEpochMilli();
        when(redisService.get("verification:" + testEmail)).thenReturn("ABC123:" + oldTimestamp);

        boolean result = verificationCodeService.canResend(testEmail);

        assertTrue(result);
    }

    @Test
    void shouldNotAllowResendWithinCooldownPeriod() {
        // Create a timestamp that is within the 30-second cooldown
        long recentTimestamp = Instant.now().minusSeconds(15).toEpochMilli();
        when(redisService.get("verification:" + testEmail)).thenReturn("ABC123:" + recentTimestamp);

        boolean result = verificationCodeService.canResend(testEmail);

        assertFalse(result);
    }

    @Test
    void shouldDeleteCode() {
        doNothing().when(redisService).delete("verification:" + testEmail);

        verificationCodeService.deleteCode(testEmail);

        verify(redisService).delete("verification:" + testEmail);
    }

    @Test
    void shouldVerifyCorrectCode() {
        String correctCode = "ABC123";
        when(redisService.get("verification:" + testEmail)).thenReturn(correctCode + ":1234567890123");
        doNothing().when(redisService).delete("verification:" + testEmail);

        boolean result = verificationCodeService.verify(testEmail, correctCode);

        assertTrue(result);
        verify(redisService).delete("verification:" + testEmail);
    }

    @Test
    void shouldVerifyCorrectCodeCaseInsensitive() {
        String correctCode = "ABC123";
        when(redisService.get("verification:" + testEmail)).thenReturn(correctCode + ":1234567890123");
        doNothing().when(redisService).delete("verification:" + testEmail);

        boolean result = verificationCodeService.verify(testEmail, "abc123");

        assertTrue(result);
        verify(redisService).delete("verification:" + testEmail);
    }

    @Test
    void shouldNotVerifyIncorrectCode() {
        when(redisService.get("verification:" + testEmail)).thenReturn("ABC123:1234567890123");

        boolean result = verificationCodeService.verify(testEmail, "WRONG1");

        assertFalse(result);
    }

    @Test
    void shouldNotVerifyWhenNoCodeExists() {
        when(redisService.get("verification:" + testEmail)).thenReturn(null);

        boolean result = verificationCodeService.verify(testEmail, "ABC123");

        assertFalse(result);
    }
}
