package com.smoothtravel.user.service;

import com.smoothtravel.user.dto.CreateUserRequest;
import com.smoothtravel.user.dto.UserResponse;
import com.smoothtravel.user.entity.User;
import com.smoothtravel.user.exception.EmailAlreadyExistsException;
import com.smoothtravel.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.id = UUID.randomUUID();
        existingUser.email = "test@example.com";
        existingUser.verified = false;
        existingUser.createdAt = Instant.now();
        existingUser.updatedAt = Instant.now();
    }

    @Test
    void shouldCreateUser() {
        CreateUserRequest request = new CreateUserRequest("new@example.com");
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.id = UUID.randomUUID();
            user.createdAt = Instant.now();
            user.updatedAt = Instant.now();
            return null;
        }).when(userRepository).persist(any(User.class));

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("new@example.com", response.email());
        assertFalse(response.verified());
        verify(userRepository).persist(any(User.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(request));
        verify(userRepository, never()).persist(any(User.class));
    }

    @Test
    void shouldReturnUserById() {
        when(userRepository.findByIdOptional(existingUser.id)).thenReturn(Optional.of(existingUser));

        Optional<UserResponse> result = userService.getUserById(existingUser.id);

        assertTrue(result.isPresent());
        assertEquals(existingUser.email, result.get().email());
        assertEquals(existingUser.id, result.get().id());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(userRepository.findByIdOptional(unknownId)).thenReturn(Optional.empty());

        Optional<UserResponse> result = userService.getUserById(unknownId);

        assertTrue(result.isEmpty());
    }
}
