package com.smoothtravel.health.service;

import com.smoothtravel.health.dto.HealthResponse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class HealthServiceTest {

    @Inject
    HealthService healthService;

    @Test
    void shouldReturnOkStatusWithComponents() {
        HealthResponse response = healthService.getStatus();

        assertEquals("ok", response.status());
        assertNotNull(response.components());
        assertTrue(response.components().containsKey("database"));
        assertEquals("UP", response.components().get("database").status());
    }
}
