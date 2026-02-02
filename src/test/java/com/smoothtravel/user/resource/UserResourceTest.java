package com.smoothtravel.user.resource;

import com.smoothtravel.auth.service.TokenBlacklistService;
import com.smoothtravel.user.dto.UserResponse;
import com.smoothtravel.user.service.UserService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserResourceTest {

    @Mock
    UserService userService;

    @Mock
    TokenBlacklistService tokenBlacklistService;

    @Mock
    JsonWebToken jwt;

    @InjectMocks
    UserResource userResource;

    private final UUID testUserId = UUID.randomUUID();

    @Test
    void shouldReturnCurrentUser() {
        UserResponse userResponse = new UserResponse(
                testUserId,
                "test@example.com",
                true,
                Instant.now(),
                Instant.now()
        );
        when(jwt.getSubject()).thenReturn(testUserId.toString());
        when(userService.getUserById(testUserId)).thenReturn(Optional.of(userResponse));

        Response response = userResource.me();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(userService).getUserById(testUserId);
    }

    @Test
    void shouldDeleteCurrentUser() {
        when(jwt.getSubject()).thenReturn(testUserId.toString());
        when(jwt.getTokenID()).thenReturn("test-jti");
        when(jwt.getExpirationTime()).thenReturn(Instant.now().plusSeconds(3600).getEpochSecond());
        doNothing().when(userService).deleteUser(testUserId);
        doNothing().when(tokenBlacklistService).blacklist("test-jti", 3600L);

        Response response = userResource.deleteAccount();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(userService).deleteUser(testUserId);
        verify(tokenBlacklistService).blacklist("test-jti", 3600L);
    }

    @Test
    void shouldReturnUserById() {
        UserResponse userResponse = new UserResponse(
                testUserId,
                "test@example.com",
                true,
                Instant.now(),
                Instant.now()
        );
        when(userService.getUserById(testUserId)).thenReturn(Optional.of(userResponse));

        Response response = userResource.getUserById(testUserId);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(userService).getUserById(testUserId);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() {
        when(userService.getUserById(testUserId)).thenReturn(Optional.empty());

        Response response = userResource.getUserById(testUserId);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(userService).getUserById(testUserId);
    }
}
