package com.smoothtravel.user.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class UserExceptionMapper {

    @Provider
    public static class AlreadyVerifiedMapper implements jakarta.ws.rs.ext.ExceptionMapper<AlreadyVerifiedException> {
        @Override
        public Response toResponse(AlreadyVerifiedException e) {
            return Response.status(Response.Status.CONFLICT)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @Provider
    public static class VerificationPendingMapper implements jakarta.ws.rs.ext.ExceptionMapper<VerificationPendingException> {
        @Override
        public Response toResponse(VerificationPendingException e) {
            return Response.status(Response.Status.CONFLICT)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @Provider
    public static class UserNotFoundMapper implements jakarta.ws.rs.ext.ExceptionMapper<UserNotFoundException> {
        @Override
        public Response toResponse(UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @Provider
    public static class ResendCooldownMapper implements jakarta.ws.rs.ext.ExceptionMapper<ResendCooldownException> {
        @Override
        public Response toResponse(ResendCooldownException e) {
            return Response.status(429)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @Provider
    public static class InvalidVerificationCodeMapper implements jakarta.ws.rs.ext.ExceptionMapper<InvalidVerificationCodeException> {
        @Override
        public Response toResponse(InvalidVerificationCodeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @Provider
    public static class UserNotVerifiedMapper implements jakarta.ws.rs.ext.ExceptionMapper<UserNotVerifiedException> {
        @Override
        public Response toResponse(UserNotVerifiedException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}
