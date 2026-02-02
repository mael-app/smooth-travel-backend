package com.smoothtravel.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VerifyCodeRequestTest {

    @Test
    void shouldCreateVerifyCodeRequest() {
        String email = "test@example.com";
        String code = "123456";

        VerifyCodeRequest request = new VerifyCodeRequest(email, code);

        assertEquals(email, request.email());
        assertEquals(code, request.code());
    }
}
