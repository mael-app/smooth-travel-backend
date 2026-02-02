package com.smoothtravel.passkey.resource;

import com.smoothtravel.auth.service.TokenService;
import com.smoothtravel.passkey.dto.PasskeyAuthStartRequest;
import com.smoothtravel.passkey.exception.PasskeyAuthenticationException;
import com.smoothtravel.passkey.service.PasskeyService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasskeyResourceTest {

    @Mock
    PasskeyService passkeyService;

    @Mock
    TokenService tokenService;

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
}
