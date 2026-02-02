package com.smoothtravel.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateUserRequestTest {

    @Test
    void shouldCreateCreateUserRequest() {
        String email = "test@example.com";

        CreateUserRequest request = new CreateUserRequest(email);

        assertEquals(email, request.email());
    }
}
