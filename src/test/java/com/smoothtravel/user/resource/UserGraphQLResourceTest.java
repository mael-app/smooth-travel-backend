package com.smoothtravel.user.resource;

import com.smoothtravel.user.dto.CreateUserRequest;
import com.smoothtravel.user.dto.UserResponse;
import com.smoothtravel.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserGraphQLResourceTest {

    @Mock
    UserService userService;

    @InjectMocks
    UserGraphQLResource userGraphQLResource;

    private final UUID testUserId = UUID.randomUUID();

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

        UserResponse result = userGraphQLResource.getUserById(testUserId.toString());

        assertEquals(userResponse, result);
        verify(userService).getUserById(testUserId);
    }

    @Test
    void shouldReturnNullWhenUserByIdNotFound() {
        when(userService.getUserById(testUserId)).thenReturn(Optional.empty());

        UserResponse result = userGraphQLResource.getUserById(testUserId.toString());

        assertNull(result);
        verify(userService).getUserById(testUserId);
    }

    @Test
    void shouldReturnUserByEmail() {
        UserResponse userResponse = new UserResponse(
                testUserId,
                "test@example.com",
                true,
                Instant.now(),
                Instant.now()
        );
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(userResponse));

        UserResponse result = userGraphQLResource.getUserByEmail("test@example.com");

        assertEquals(userResponse, result);
        verify(userService).getUserByEmail("test@example.com");
    }

    @Test
    void shouldReturnNullWhenUserByEmailNotFound() {
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.empty());

        UserResponse result = userGraphQLResource.getUserByEmail("test@example.com");

        assertNull(result);
        verify(userService).getUserByEmail("test@example.com");
    }

    @Test
    void shouldCreateUser() {
        CreateUserRequest request = new CreateUserRequest("test@example.com");
        UserResponse userResponse = new UserResponse(
                testUserId,
                "test@example.com",
                false,
                Instant.now(),
                Instant.now()
        );
        when(userService.createUser(request)).thenReturn(userResponse);

        UserResponse result = userGraphQLResource.createUser(request);

        assertEquals(userResponse, result);
        verify(userService).createUser(request);
    }

    @Test
    void shouldResendVerificationCode() {
        doNothing().when(userService).resendVerificationCode("test@example.com");

        boolean result = userGraphQLResource.resendVerificationCode("test@example.com");

        assertTrue(result);
        verify(userService).resendVerificationCode("test@example.com");
    }

    @Test
    void shouldVerifyEmail() {
        UserResponse userResponse = new UserResponse(
                testUserId,
                "test@example.com",
                true,
                Instant.now(),
                Instant.now()
        );
        when(userService.verifyUser("test@example.com", "123456")).thenReturn(userResponse);

        UserResponse result = userGraphQLResource.verifyEmail("test@example.com", "123456");

        assertEquals(userResponse, result);
        verify(userService).verifyUser("test@example.com", "123456");
    }
}
