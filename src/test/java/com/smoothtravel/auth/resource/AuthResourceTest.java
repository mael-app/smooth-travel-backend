package com.smoothtravel.auth.resource;

import com.smoothtravel.auth.service.TokenBlacklistService;
import com.smoothtravel.auth.service.TokenService;
import com.smoothtravel.user.dto.CreateUserRequest;
import com.smoothtravel.user.dto.LoginRequest;
import com.smoothtravel.user.dto.ResendCodeRequest;
import com.smoothtravel.user.dto.UserResponse;
import com.smoothtravel.user.dto.VerifyCodeRequest;
import com.smoothtravel.user.service.UserService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthResourceTest {

    @Mock
    UserService userService;

    @Mock
    TokenService tokenService;

    @Mock
    TokenBlacklistService tokenBlacklistService;

    @Mock
    JsonWebToken jwt;

    @InjectMocks
    AuthResource authResource;

    @Test
    void shouldRegisterUser() {
        CreateUserRequest request = new CreateUserRequest("test@example.com");
        UserResponse userResponse = new UserResponse(
                UUID.randomUUID(),
                "test@example.com",
                false,
                Instant.now(),
                Instant.now()
        );
        when(userService.createUser(request)).thenReturn(userResponse);

        Response response = authResource.register(request);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void shouldVerifyRegistration() {
        VerifyCodeRequest request = new VerifyCodeRequest("test@example.com", "123456");
        UserResponse userResponse = new UserResponse(
                UUID.randomUUID(),
                "test@example.com",
                true,
                Instant.now(),
                Instant.now()
        );
        when(userService.verifyUser(request.email(), request.code())).thenReturn(userResponse);
        when(tokenService.generateToken(userResponse.id(), userResponse.email())).thenReturn("jwt-token");

        Response response = authResource.verifyRegistration(request);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void shouldLogin() {
        LoginRequest request = new LoginRequest("test@example.com");
        doNothing().when(userService).loginUser(request.email());

        Response response = authResource.login(request);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(userService).loginUser(request.email());
    }

    @Test
    void shouldVerifyLogin() {
        VerifyCodeRequest request = new VerifyCodeRequest("test@example.com", "123456");
        UserResponse userResponse = new UserResponse(
                UUID.randomUUID(),
                "test@example.com",
                true,
                Instant.now(),
                Instant.now()
        );
        when(userService.verifyLogin(request.email(), request.code())).thenReturn(userResponse);
        when(tokenService.generateToken(userResponse.id(), userResponse.email())).thenReturn("jwt-token");

        Response response = authResource.verifyLogin(request);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void shouldResendCode() {
        ResendCodeRequest request = new ResendCodeRequest("test@example.com");
        doNothing().when(userService).resendVerificationCode(request.email());

        Response response = authResource.resendCode(request);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(userService).resendVerificationCode(request.email());
    }

    @Test
    void shouldLogout() {
        when(jwt.getTokenID()).thenReturn("test-jti");
        when(jwt.getExpirationTime()).thenReturn(Instant.now().plusSeconds(3600).getEpochSecond());
        doNothing().when(tokenBlacklistService).blacklist("test-jti", 3600L);

        Response response = authResource.logout();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(tokenBlacklistService).blacklist("test-jti", 3600L);
    }
}
