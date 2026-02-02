package com.smoothtravel.passkey.service;

import com.smoothtravel.infrastructure.redis.RedisService;
import com.smoothtravel.passkey.dto.PasskeyRegistrationStartRequest;
import com.smoothtravel.passkey.exception.PasskeyChallengeExpiredException;
import com.smoothtravel.user.exception.UserNotVerifiedException;
import com.smoothtravel.passkey.repository.PasskeyCredentialRepository;
import com.smoothtravel.user.entity.User;
import com.smoothtravel.user.exception.UserNotFoundException;
import com.smoothtravel.user.repository.UserRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasskeyServiceTest {

    @Mock
    RelyingParty relyingParty;

    @Mock
    UserRepository userRepository;

    @Mock
    PasskeyCredentialRepository passkeyCredentialRepository;

    @Mock
    RedisService redisService;

    @InjectMocks
    PasskeyService passkeyService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.id = UUID.randomUUID();
        user.email = "test@example.com";
        user.verified = true;
        user.createdAt = Instant.now();
        user.updatedAt = Instant.now();
    }

    @Test
    void shouldThrowWhenUserNotFoundOnStartRegistration() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findByIdOptional(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> passkeyService.startRegistration(userId, new PasskeyRegistrationStartRequest(null)));
    }

    @Test
    void shouldThrowWhenUserNotVerifiedOnStartRegistration() {
        user.verified = false;
        when(userRepository.findByIdOptional(user.id)).thenReturn(Optional.of(user));

        assertThrows(UserNotVerifiedException.class,
                () -> passkeyService.startRegistration(user.id, new PasskeyRegistrationStartRequest(null)));
    }

    @Test
    void shouldThrowWhenChallengeExpiredOnFinishRegistration() {
        UUID userId = UUID.randomUUID();
        when(redisService.get("passkey:reg:" + userId)).thenReturn(null);

        assertThrows(PasskeyChallengeExpiredException.class,
                () -> passkeyService.finishRegistration(userId, "{}"));
    }

    @Test
    void shouldThrowWhenChallengeExpiredOnFinishAuthentication() {
        when(redisService.get("passkey:auth:some-request-id")).thenReturn(null);

        assertThrows(PasskeyChallengeExpiredException.class,
                () -> passkeyService.finishAuthentication("some-request-id", "{}"));
    }
}
