package com.smoothtravel.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginRequestTest {

    @Test
    void shouldCreateLoginRequest() {
        String email = "test@example.com";

        LoginRequest request = new LoginRequest(email);

        assertEquals(email, request.email());
    }
}
