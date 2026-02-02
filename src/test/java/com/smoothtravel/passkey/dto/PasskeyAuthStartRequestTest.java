package com.smoothtravel.passkey.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PasskeyAuthStartRequestTest {

    @Test
    void shouldCreatePasskeyAuthStartRequest() {
        String email = "test@example.com";

        PasskeyAuthStartRequest request = new PasskeyAuthStartRequest(email);

        assertEquals(email, request.email());
    }
}
