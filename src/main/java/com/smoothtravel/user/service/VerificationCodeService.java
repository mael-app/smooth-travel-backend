package com.smoothtravel.user.service;

import com.smoothtravel.infrastructure.mail.EmailService;
import com.smoothtravel.infrastructure.redis.RedisService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

@ApplicationScoped
public class VerificationCodeService {

    private static final String KEY_PREFIX = "verification:";
    private static final Duration CODE_TTL = Duration.ofMinutes(15);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(30);
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;

    private final SecureRandom random = new SecureRandom();

    @Inject
    RedisService redisService;

    @Inject
    EmailService emailService;

    public void generateAndSend(String email) {
        String code = generateCode();
        String value = code + ":" + Instant.now().toEpochMilli();
        redisService.set(KEY_PREFIX + email, value, CODE_TTL);
        sendVerificationEmail(email, code);
    }

    public boolean hasPendingCode(String email) {
        return redisService.get(KEY_PREFIX + email) != null;
    }

    public boolean canResend(String email) {
        String value = redisService.get(KEY_PREFIX + email);
        if (value == null) {
            return true;
        }
        long createdAt = Long.parseLong(value.split(":")[1]);
        return Instant.now().isAfter(Instant.ofEpochMilli(createdAt).plus(RESEND_COOLDOWN));
    }

    public void deleteCode(String email) {
        redisService.delete(KEY_PREFIX + email);
    }

    public boolean verify(String email, String code) {
        String value = redisService.get(KEY_PREFIX + email);
        if (value == null) {
            return false;
        }
        String storedCode = value.split(":")[0];
        if (storedCode.equalsIgnoreCase(code)) {
            redisService.delete(KEY_PREFIX + email);
            return true;
        }
        return false;
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private void sendVerificationEmail(String email, String code) {
        String html = """
                <div style="font-family: sans-serif; max-width: 480px; margin: 0 auto;">
                    <h2>Verify your email</h2>
                    <p>Your verification code is:</p>
                    <div style="font-size: 32px; font-weight: bold; letter-spacing: 8px; \
                text-align: center; padding: 16px; background: #f4f4f4; border-radius: 8px;">
                        %s
                    </div>
                    <p style="color: #666; margin-top: 16px;">This code expires in 15 minutes.</p>
                </div>
                """.formatted(code);
        emailService.sendHtml(email, "SmoothTravel - Verification code", html);
    }
}
