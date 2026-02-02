package com.smoothtravel.infrastructure.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionMapperTest {

    @Test
    void shouldMapGenericException() {
        GlobalExceptionMapper mapper = new GlobalExceptionMapper();
        RuntimeException exception = new RuntimeException("Something went wrong");

        Response response = mapper.toResponse(exception);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertNotNull(response.getEntity());
    }
}
