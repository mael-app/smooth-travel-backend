package com.smoothtravel.user.service;

import com.smoothtravel.user.dto.CreateUserRequest;
import com.smoothtravel.user.dto.UserResponse;
import com.smoothtravel.user.entity.User;
import com.smoothtravel.user.exception.EmailAlreadyExistsException;
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

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(existing -> {
            throw new EmailAlreadyExistsException(request.email());
        });

        User user = new User();
        user.email = request.email();
        user.verified = false;
        userRepository.persist(user);

        return toResponse(user);
    }

    public Optional<UserResponse> getUserById(UUID id) {
        return userRepository.findByIdOptional(id).map(this::toResponse);
    }

    public Optional<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toResponse);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.id, user.email, user.verified, user.createdAt, user.updatedAt);
    }
}
