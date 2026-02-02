package com.smoothtravel.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResendCodeRequestTest {

    @Test
    void shouldCreateResendCodeRequest() {
        String email = "test@example.com";

        ResendCodeRequest request = new ResendCodeRequest(email);

        assertEquals(email, request.email());
    }
}
