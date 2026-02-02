package com.smoothtravel.user.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserExceptionMapperTest {

    @Test
    void shouldMapAlreadyVerifiedException() {
        UserExceptionMapper.AlreadyVerifiedMapper mapper = new UserExceptionMapper.AlreadyVerifiedMapper();
        AlreadyVerifiedException exception = new AlreadyVerifiedException("test@example.com");

        Response response = mapper.toResponse(exception);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertNotNull(response.getEntity());
    }

    @Test
    void shouldMapVerificationPendingException() {
        UserExceptionMapper.VerificationPendingMapper mapper = new UserExceptionMapper.VerificationPendingMapper();
        VerificationPendingException exception = new VerificationPendingException("test@example.com");

        Response response = mapper.toResponse(exception);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertNotNull(response.getEntity());
    }

    @Test
    void shouldMapUserNotFoundException() {
        UserExceptionMapper.UserNotFoundMapper mapper = new UserExceptionMapper.UserNotFoundMapper();
        UserNotFoundException exception = new UserNotFoundException("test@example.com");

        Response response = mapper.toResponse(exception);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertNotNull(response.getEntity());
    }

    @Test
    void shouldMapResendCooldownException() {
        UserExceptionMapper.ResendCooldownMapper mapper = new UserExceptionMapper.ResendCooldownMapper();
        ResendCooldownException exception = new ResendCooldownException();

        Response response = mapper.toResponse(exception);

        assertEquals(Response.Status.TOO_MANY_REQUESTS.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertNotNull(response.getEntity());
    }

    @Test
    void shouldMapInvalidVerificationCodeException() {
        UserExceptionMapper.InvalidVerificationCodeMapper mapper = new UserExceptionMapper.InvalidVerificationCodeMapper();
        InvalidVerificationCodeException exception = new InvalidVerificationCodeException();

        Response response = mapper.toResponse(exception);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertNotNull(response.getEntity());
    }

    @Test
    void shouldMapUserNotVerifiedException() {
        UserExceptionMapper.UserNotVerifiedMapper mapper = new UserExceptionMapper.UserNotVerifiedMapper();
        UserNotVerifiedException exception = new UserNotVerifiedException("test@example.com");

        Response response = mapper.toResponse(exception);

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertNotNull(response.getEntity());
    }
}
