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
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    VerificationCodeService verificationCodeService;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        Optional<User> existing = userRepository.findByEmail(request.email());

        if (existing.isPresent()) {
            User user = existing.get();
            if (user.verified) {
                throw new AlreadyVerifiedException(request.email());
            }
            if (verificationCodeService.hasPendingCode(request.email())) {
                throw new VerificationPendingException(request.email());
            }
            verificationCodeService.generateAndSend(request.email());
            return toResponse(user);
        }

        User user = new User();
        user.email = request.email();
        user.verified = false;
        userRepository.persist(user);

        verificationCodeService.generateAndSend(request.email());

        return toResponse(user);
    }

    public void resendVerificationCode(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (!verificationCodeService.canResend(email)) {
            throw new ResendCooldownException();
        }

        verificationCodeService.generateAndSend(email);
    }

    @Transactional
    public UserResponse verifyUser(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (user.verified) {
            throw new AlreadyVerifiedException(email);
        }

        if (!verificationCodeService.verify(email, code)) {
            throw new InvalidVerificationCodeException();
        }

        user.verified = true;
        userRepository.persist(user);

        return toResponse(user);
    }

    public Optional<UserResponse> getUserById(UUID id) {
        return userRepository.findByIdOptional(id).map(this::toResponse);
    }

    public void loginUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (!user.verified) {
            throw new UserNotVerifiedException(email);
        }

        verificationCodeService.generateAndSend(email);
    }

    public UserResponse verifyLogin(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (!user.verified) {
            throw new UserNotVerifiedException(email);
        }

        if (!verificationCodeService.verify(email, code)) {
            throw new InvalidVerificationCodeException();
        }

        return toResponse(user);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        verificationCodeService.deleteCode(user.email);
        userRepository.delete(user);
    }

    public Optional<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toResponse);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.id, user.email, user.verified, user.createdAt, user.updatedAt);
    }
}
