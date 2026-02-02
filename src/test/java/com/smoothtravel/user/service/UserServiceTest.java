package com.smoothtravel.user.service;

import com.smoothtravel.user.dto.CreateUserRequest;
import com.smoothtravel.user.dto.UserResponse;
import com.smoothtravel.user.entity.User;
import com.smoothtravel.user.exception.AlreadyVerifiedException;
import com.smoothtravel.user.exception.InvalidVerificationCodeException;
import com.smoothtravel.user.exception.ResendCooldownException;
import com.smoothtravel.user.exception.UserNotFoundException;
import com.smoothtravel.user.exception.UserNotVerifiedException;
import com.smoothtravel.user.exception.VerificationPendingException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    VerificationCodeService verificationCodeService;

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
        doNothing().when(verificationCodeService).generateAndSend(anyString());
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
        verify(verificationCodeService).generateAndSend("new@example.com");
    }

    @Test
    void shouldThrowWhenEmailAlreadyVerified() {
        existingUser.verified = true;
        CreateUserRequest request = new CreateUserRequest("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        assertThrows(AlreadyVerifiedException.class, () -> userService.createUser(request));
        verify(userRepository, never()).persist(any(User.class));
    }

    @Test
    void shouldThrowWhenVerificationPending() {
        CreateUserRequest request = new CreateUserRequest("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(verificationCodeService.hasPendingCode("test@example.com")).thenReturn(true);

        assertThrows(VerificationPendingException.class, () -> userService.createUser(request));
    }

    @Test
    void shouldResendCodeWhenNoPendingCode() {
        CreateUserRequest request = new CreateUserRequest("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(verificationCodeService.hasPendingCode("test@example.com")).thenReturn(false);
        doNothing().when(verificationCodeService).generateAndSend(anyString());

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        verify(verificationCodeService).generateAndSend("test@example.com");
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

    @Test
    void shouldResendVerificationCode() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(verificationCodeService.canResend(email)).thenReturn(true);
        doNothing().when(verificationCodeService).generateAndSend(email);

        userService.resendVerificationCode(email);

        verify(verificationCodeService).generateAndSend(email);
    }

    @Test
    void shouldThrowUserNotFoundWhenResendingToNonExistentUser() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.resendVerificationCode(email));
    }

    @Test
    void shouldThrowResendCooldownException() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(verificationCodeService.canResend(email)).thenReturn(false);

        assertThrows(ResendCooldownException.class, () -> userService.resendVerificationCode(email));
    }

    @Test
    void shouldVerifyUser() {
        String email = "test@example.com";
        String code = "123456";
        existingUser.verified = false;
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(verificationCodeService.verify(email, code)).thenReturn(true);

        UserResponse response = userService.verifyUser(email, code);

        assertNotNull(response);
        assertEquals(email, response.email());
        assertTrue(response.verified());
        verify(userRepository).persist(existingUser);
    }

    @Test
    void shouldThrowUserNotFoundWhenVerifyingNonExistentUser() {
        String email = "nonexistent@example.com";
        String code = "123456";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.verifyUser(email, code));
    }

    @Test
    void shouldThrowAlreadyVerifiedWhenVerifyingVerifiedUser() {
        String email = "test@example.com";
        String code = "123456";
        existingUser.verified = true;
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        assertThrows(AlreadyVerifiedException.class, () -> userService.verifyUser(email, code));
    }

    @Test
    void shouldThrowInvalidVerificationCodeException() {
        String email = "test@example.com";
        String code = "wrong";
        existingUser.verified = false;
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(verificationCodeService.verify(email, code)).thenReturn(false);

        assertThrows(InvalidVerificationCodeException.class, () -> userService.verifyUser(email, code));
    }

    @Test
    void shouldLoginUser() {
        String email = "test@example.com";
        existingUser.verified = true;
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        doNothing().when(verificationCodeService).generateAndSend(email);

        userService.loginUser(email);

        verify(verificationCodeService).generateAndSend(email);
    }

    @Test
    void shouldThrowUserNotFoundWhenLoggingInNonExistentUser() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.loginUser(email));
    }

    @Test
    void shouldThrowUserNotVerifiedWhenLoggingInUnverifiedUser() {
        String email = "test@example.com";
        existingUser.verified = false;
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        assertThrows(UserNotVerifiedException.class, () -> userService.loginUser(email));
    }

    @Test
    void shouldVerifyLogin() {
        String email = "test@example.com";
        String code = "123456";
        existingUser.verified = true;
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(verificationCodeService.verify(email, code)).thenReturn(true);

        UserResponse response = userService.verifyLogin(email, code);

        assertNotNull(response);
        assertEquals(email, response.email());
        assertTrue(response.verified());
    }

    @Test
    void shouldThrowUserNotFoundWhenVerifyingLoginForNonExistentUser() {
        String email = "nonexistent@example.com";
        String code = "123456";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.verifyLogin(email, code));
    }

    @Test
    void shouldThrowUserNotVerifiedWhenVerifyingLoginForUnverifiedUser() {
        String email = "test@example.com";
        String code = "123456";
        existingUser.verified = false;
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        assertThrows(UserNotVerifiedException.class, () -> userService.verifyLogin(email, code));
    }

    @Test
    void shouldThrowInvalidVerificationCodeExceptionOnVerifyLogin() {
        String email = "test@example.com";
        String code = "wrong";
        existingUser.verified = true;
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(verificationCodeService.verify(email, code)).thenReturn(false);

        assertThrows(InvalidVerificationCodeException.class, () -> userService.verifyLogin(email, code));
    }

    @Test
    void shouldDeleteUser() {
        UUID userId = existingUser.id;
        when(userRepository.findByIdOptional(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(verificationCodeService).deleteCode(existingUser.email);

        userService.deleteUser(userId);

        verify(verificationCodeService).deleteCode(existingUser.email);
        verify(userRepository).delete(existingUser);
    }

    @Test
    void shouldThrowUserNotFoundWhenDeletingNonExistentUser() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findByIdOptional(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
    }

    @Test
    void shouldGetUserByEmail() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        Optional<UserResponse> result = userService.getUserByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().email());
        assertEquals(existingUser.id, result.get().id());
    }

    @Test
    void shouldReturnEmptyWhenUserByEmailNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<UserResponse> result = userService.getUserByEmail(email);

        assertTrue(result.isEmpty());
    }
}
