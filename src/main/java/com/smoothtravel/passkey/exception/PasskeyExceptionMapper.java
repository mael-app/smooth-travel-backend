package com.smoothtravel.passkey.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class PasskeyExceptionMapper {

    @Provider
    public static class RegistrationMapper implements jakarta.ws.rs.ext.ExceptionMapper<PasskeyRegistrationException> {
        @Override
        public Response toResponse(PasskeyRegistrationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @Provider
    public static class AuthenticationMapper implements jakarta.ws.rs.ext.ExceptionMapper<PasskeyAuthenticationException> {
        @Override
        public Response toResponse(PasskeyAuthenticationException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @Provider
    public static class ChallengeExpiredMapper implements jakarta.ws.rs.ext.ExceptionMapper<PasskeyChallengeExpiredException> {
        @Override
        public Response toResponse(PasskeyChallengeExpiredException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

}
