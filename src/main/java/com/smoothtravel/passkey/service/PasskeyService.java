package com.smoothtravel.passkey.service;

import com.smoothtravel.infrastructure.redis.RedisService;
import com.smoothtravel.passkey.dto.PasskeyAuthStartRequest;
import com.smoothtravel.passkey.dto.PasskeyRegistrationStartRequest;
import com.smoothtravel.passkey.entity.PasskeyCredential;
import com.smoothtravel.passkey.exception.PasskeyAuthenticationException;
import com.smoothtravel.passkey.exception.PasskeyChallengeExpiredException;
import com.smoothtravel.passkey.exception.PasskeyRegistrationException;
import com.smoothtravel.user.exception.UserNotVerifiedException;
import com.smoothtravel.passkey.repository.PasskeyCredentialRepository;
import com.smoothtravel.user.entity.User;
import com.smoothtravel.user.exception.UserNotFoundException;
import com.smoothtravel.user.repository.UserRepository;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.StartAssertionOptions;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.ResidentKeyRequirement;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Duration;
import java.util.UUID;

@ApplicationScoped
public class PasskeyService {

    private static final String REG_CHALLENGE_PREFIX = "passkey:reg:";
    private static final String AUTH_CHALLENGE_PREFIX = "passkey:auth:";
    private static final Duration CHALLENGE_TTL = Duration.ofMinutes(5);

    @Inject
    RelyingParty relyingParty;

    @Inject
    UserRepository userRepository;

    @Inject
    PasskeyCredentialRepository passkeyCredentialRepository;

    @Inject
    RedisService redisService;

    public String startRegistration(UUID userId, PasskeyRegistrationStartRequest request) {
        User user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        if (!user.verified) {
            throw new UserNotVerifiedException(user.email);
        }

        UserIdentity userIdentity = UserIdentity.builder()
                .name(user.email)
                .displayName(request.displayName() != null ? request.displayName() : user.email)
                .id(new ByteArray(PasskeyCredentialRegistryAdapter.uuidToBytes(user.id)))
                .build();

        StartRegistrationOptions options = StartRegistrationOptions.builder()
                .user(userIdentity)
                .authenticatorSelection(AuthenticatorSelectionCriteria.builder()
                        .residentKey(ResidentKeyRequirement.PREFERRED)
                        .build())
                .build();

        PublicKeyCredentialCreationOptions creationOptions = relyingParty.startRegistration(options);

        try {
            redisService.set(REG_CHALLENGE_PREFIX + userId, creationOptions.toJson(), CHALLENGE_TTL);
            return creationOptions.toCredentialsCreateJson();
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new PasskeyRegistrationException("Failed to serialize registration options: " + e.getMessage());
        }
    }

    @Transactional
    public void finishRegistration(UUID userId, String responseJson) {
        String storedJson = redisService.get(REG_CHALLENGE_PREFIX + userId);
        if (storedJson == null) {
            throw new PasskeyChallengeExpiredException();
        }

        try {
            PublicKeyCredentialCreationOptions creationOptions =
                    PublicKeyCredentialCreationOptions.fromJson(storedJson);

            PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> credential =
                    PublicKeyCredential.parseRegistrationResponseJson(responseJson);

            RegistrationResult result = relyingParty.finishRegistration(
                    FinishRegistrationOptions.builder()
                            .request(creationOptions)
                            .response(credential)
                            .build());

            User user = userRepository.findByIdOptional(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId.toString()));

            PasskeyCredential passkeyCredential = new PasskeyCredential();
            passkeyCredential.user = user;
            passkeyCredential.credentialId = result.getKeyId().getId().getBytes();
            passkeyCredential.publicKeyCose = result.getPublicKeyCose().getBytes();
            passkeyCredential.signatureCount = result.getSignatureCount();
            passkeyCredential.discoverable = result.isDiscoverable().orElse(false);
            passkeyCredentialRepository.persist(passkeyCredential);

            redisService.delete(REG_CHALLENGE_PREFIX + userId);
        } catch (RegistrationFailedException e) {
            throw new PasskeyRegistrationException("Registration verification failed: " + e.getMessage());
        } catch (Exception e) {
            throw new PasskeyRegistrationException("Registration failed: " + e.getMessage());
        }
    }

    public String startAuthentication(PasskeyAuthStartRequest request) {
        StartAssertionOptions.StartAssertionOptionsBuilder optionsBuilder = StartAssertionOptions.builder();

        if (request != null && request.email() != null && !request.email().isBlank()) {
            optionsBuilder.username(request.email());
        }

        AssertionRequest assertionRequest = relyingParty.startAssertion(optionsBuilder.build());

        try {
            String json = assertionRequest.toCredentialsGetJson();
            String requestId = UUID.randomUUID().toString();
            redisService.set(AUTH_CHALLENGE_PREFIX + requestId, assertionRequest.toJson(), CHALLENGE_TTL);
            return "{\"requestId\":\"" + requestId + "\",\"publicKeyCredentialRequestOptions\":" + json + "}";
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new PasskeyAuthenticationException("Failed to serialize authentication options: " + e.getMessage());
        }
    }

    @Transactional
    public AuthResult finishAuthentication(String requestId, String responseJson) {
        String storedJson = redisService.get(AUTH_CHALLENGE_PREFIX + requestId);
        if (storedJson == null) {
            throw new PasskeyChallengeExpiredException();
        }

        try {
            AssertionRequest assertionRequest = AssertionRequest.fromJson(storedJson);

            PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> credential =
                    PublicKeyCredential.parseAssertionResponseJson(responseJson);

            AssertionResult result = relyingParty.finishAssertion(
                    FinishAssertionOptions.builder()
                            .request(assertionRequest)
                            .response(credential)
                            .build());

            if (!result.isSuccess()) {
                throw new PasskeyAuthenticationException("Authentication verification failed");
            }

            // Update signature count
            passkeyCredentialRepository.findByCredentialId(result.getCredential().getCredentialId().getBytes())
                    .ifPresent(cred -> {
                        cred.signatureCount = result.getSignatureCount();
                        passkeyCredentialRepository.persist(cred);
                    });

            UUID userId = PasskeyCredentialRegistryAdapter.bytesToUuid(
                    result.getCredential().getUserHandle().getBytes());
            String username = result.getUsername();

            redisService.delete(AUTH_CHALLENGE_PREFIX + requestId);

            return new AuthResult(userId, username);
        } catch (PasskeyAuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new PasskeyAuthenticationException("Authentication failed: " + e.getMessage());
        }
    }

    public record AuthResult(UUID userId, String email) {
    }
}
