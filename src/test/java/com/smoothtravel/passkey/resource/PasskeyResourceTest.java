package com.smoothtravel.passkey.resource;

import com.smoothtravel.auth.service.TokenService;
import com.smoothtravel.passkey.dto.PasskeyAuthStartRequest;
import com.smoothtravel.passkey.dto.PasskeyRegistrationStartRequest;
import com.smoothtravel.passkey.exception.PasskeyAuthenticationException;
import com.smoothtravel.passkey.exception.PasskeyChallengeExpiredException;
import com.smoothtravel.passkey.exception.PasskeyRegistrationException;
import com.smoothtravel.passkey.service.PasskeyService;
import com.smoothtravel.user.exception.UserNotFoundException;
import com.smoothtravel.user.exception.UserNotVerifiedException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasskeyResourceTest {

    @Mock
    PasskeyService passkeyService;

    @Mock
    TokenService tokenService;

    @Mock
    JsonWebToken jwt;

    @Mock
    SecurityContext securityContext;

    @InjectMocks
    PasskeyResource passkeyResource;

    private final UUID testUserId = UUID.randomUUID();

    @Test
    void shouldStartAuthentication() {
        PasskeyAuthStartRequest request = new PasskeyAuthStartRequest("test@example.com");
        String assertionJson = "{\"requestId\":\"test-id\",\"publicKeyCredentialRequestOptions\":{}}";
        
        when(passkeyService.startAuthentication(request)).thenReturn(assertionJson);

        Response response = passkeyResource.startAuthentication(request);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(passkeyService).startAuthentication(request);
    }

    @Test
    void shouldPropagatePasskeyAuthenticationExceptionOnStartAuth() {
        PasskeyAuthStartRequest request = new PasskeyAuthStartRequest("test@example.com");
        
        when(passkeyService.startAuthentication(request))
                .thenThrow(new PasskeyAuthenticationException("Authentication failed"));

        assertThrows(PasskeyAuthenticationException.class, () -> 
                passkeyResource.startAuthentication(request));
    }

    @Test
    void shouldHandleInvalidJsonInFinishAuthentication() {
        String invalidJson = "invalid-json";

        assertThrows(PasskeyAuthenticationException.class, () -> 
                passkeyResource.finishAuthentication(invalidJson));
    }

    @Test
    void shouldFinishAuthenticationWithValidJson() {
        String responseJson = "{\"requestId\":\"test-request-id\",\"credential\":{\"id\":\"test-id\",\"response\":{}}}";
        String credentialJson = "{\"id\":\"test-id\",\"response\":{}}";
        PasskeyService.AuthResult authResult = new PasskeyService.AuthResult(testUserId, "test@example.com");
        String expectedToken = "jwt-token";
        
        when(passkeyService.finishAuthentication("test-request-id", credentialJson)).thenReturn(authResult);
        when(tokenService.generateToken(testUserId, "test@example.com")).thenReturn(expectedToken);

        Response response = passkeyResource.finishAuthentication(responseJson);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(passkeyService).finishAuthentication("test-request-id", credentialJson);
        verify(tokenService).generateToken(testUserId, "test@example.com");
    }

    @Test
    void shouldStartRegistrationSuccessfully() {
        PasskeyRegistrationStartRequest request = new PasskeyRegistrationStartRequest("Display Name");
        String registrationOptions = "{\"challenge\":\"test-challenge\",\"rp\":{\"name\":\"test\"}}";
        
        when(jwt.getSubject()).thenReturn(testUserId.toString());
        when(passkeyService.startRegistration(testUserId, request)).thenReturn(registrationOptions);

        Response response = passkeyResource.startRegistration(request, securityContext);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(passkeyService).startRegistration(testUserId, request);
    }

    @Test
    void shouldStartRegistrationWithNullRequest() {
        String registrationOptions = "{\"challenge\":\"test-challenge\",\"rp\":{\"name\":\"test\"}}";
        
        when(jwt.getSubject()).thenReturn(testUserId.toString());
        when(passkeyService.startRegistration(eq(testUserId), any(PasskeyRegistrationStartRequest.class)))
                .thenReturn(registrationOptions);

        Response response = passkeyResource.startRegistration(null, securityContext);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(passkeyService).startRegistration(eq(testUserId), any(PasskeyRegistrationStartRequest.class));
    }

    @Test
    void shouldPropagateUserNotFoundExceptionOnStartRegistration() {
        PasskeyRegistrationStartRequest request = new PasskeyRegistrationStartRequest("Display Name");
        
        when(jwt.getSubject()).thenReturn(testUserId.toString());
        when(passkeyService.startRegistration(testUserId, request))
                .thenThrow(new UserNotFoundException(testUserId.toString()));

        assertThrows(UserNotFoundException.class, () ->
                passkeyResource.startRegistration(request, securityContext));
    }

    @Test
    void shouldPropagateUserNotVerifiedExceptionOnStartRegistration() {
        PasskeyRegistrationStartRequest request = new PasskeyRegistrationStartRequest("Display Name");
        
        when(jwt.getSubject()).thenReturn(testUserId.toString());
        when(passkeyService.startRegistration(testUserId, request))
                .thenThrow(new UserNotVerifiedException("test@example.com"));

        assertThrows(UserNotVerifiedException.class, () ->
                passkeyResource.startRegistration(request, securityContext));
    }

    @Test
    void shouldPropagatePasskeyRegistrationExceptionOnStartRegistration() {
        PasskeyRegistrationStartRequest request = new PasskeyRegistrationStartRequest("Display Name");
        
        when(jwt.getSubject()).thenReturn(testUserId.toString());
        when(passkeyService.startRegistration(testUserId, request))
                .thenThrow(new PasskeyRegistrationException("Registration failed"));

        assertThrows(PasskeyRegistrationException.class, () ->
                passkeyResource.startRegistration(request, securityContext));
    }

    @Test
    void shouldFinishRegistrationSuccessfully() {
        String responseJson = "{\"id\":\"credential-id\",\"response\":{\"attestationObject\":\"test\"}}";
        
        when(jwt.getSubject()).thenReturn(testUserId.toString());
        doNothing().when(passkeyService).finishRegistration(testUserId, responseJson);

        Response response = passkeyResource.finishRegistration(responseJson, securityContext);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(passkeyService).finishRegistration(testUserId, responseJson);
    }

    @Test
    void shouldPropagatePasskeyChallengeExpiredExceptionOnFinishRegistration() {
        String responseJson = "{\"id\":\"credential-id\",\"response\":{\"attestationObject\":\"test\"}}";
        
        when(jwt.getSubject()).thenReturn(testUserId.toString());
        doThrow(new PasskeyChallengeExpiredException())
                .when(passkeyService).finishRegistration(testUserId, responseJson);

        assertThrows(PasskeyChallengeExpiredException.class, () ->
                passkeyResource.finishRegistration(responseJson, securityContext));
    }

    @Test
    void shouldPropagatePasskeyRegistrationExceptionOnFinishRegistration() {
        String responseJson = "{\"id\":\"credential-id\",\"response\":{\"attestationObject\":\"test\"}}";
        
        when(jwt.getSubject()).thenReturn(testUserId.toString());
        doThrow(new PasskeyRegistrationException("Registration failed"))
                .when(passkeyService).finishRegistration(testUserId, responseJson);

        assertThrows(PasskeyRegistrationException.class, () ->
                passkeyResource.finishRegistration(responseJson, securityContext));
    }

    @Test
    void shouldPropagatePasskeyChallengeExpiredExceptionOnFinishAuthentication() {
        String responseJson = "{\"requestId\":\"test-request-id\",\"credential\":{\"id\":\"test-id\",\"response\":{}}}";
        String credentialJson = "{\"id\":\"test-id\",\"response\":{}}";
        
        when(passkeyService.finishAuthentication("test-request-id", credentialJson))
                .thenThrow(new PasskeyChallengeExpiredException());

        assertThrows(PasskeyChallengeExpiredException.class, () ->
                passkeyResource.finishAuthentication(responseJson));
    }

    @Test
    void shouldStartAuthenticationWithNullRequest() {
        String assertionJson = "{\"requestId\":\"test-id\",\"publicKeyCredentialRequestOptions\":{}}";
        
        when(passkeyService.startAuthentication(null)).thenReturn(assertionJson);

        Response response = passkeyResource.startAuthentication(null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(passkeyService).startAuthentication(null);
    }
}
