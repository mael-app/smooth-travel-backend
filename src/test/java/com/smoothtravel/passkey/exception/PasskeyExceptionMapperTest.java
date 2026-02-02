package com.smoothtravel.passkey.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PasskeyExceptionMapperTest {

    @Test
    void shouldMapPasskeyRegistrationException() {
        PasskeyExceptionMapper.RegistrationMapper mapper = new PasskeyExceptionMapper.RegistrationMapper();
        PasskeyRegistrationException exception = new PasskeyRegistrationException("Registration failed");

        Response response = mapper.toResponse(exception);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertNotNull(response.getEntity());
    }

    @Test
    void shouldMapPasskeyAuthenticationException() {
        PasskeyExceptionMapper.AuthenticationMapper mapper = new PasskeyExceptionMapper.AuthenticationMapper();
        PasskeyAuthenticationException exception = new PasskeyAuthenticationException("Authentication failed");

        Response response = mapper.toResponse(exception);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertNotNull(response.getEntity());
    }

    @Test
    void shouldMapPasskeyChallengeExpiredException() {
        PasskeyExceptionMapper.ChallengeExpiredMapper mapper = new PasskeyExceptionMapper.ChallengeExpiredMapper();
        PasskeyChallengeExpiredException exception = new PasskeyChallengeExpiredException();

        Response response = mapper.toResponse(exception);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertNotNull(response.getEntity());
    }
}
