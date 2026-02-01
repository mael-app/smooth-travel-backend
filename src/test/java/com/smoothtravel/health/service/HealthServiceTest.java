package com.smoothtravel.health.service;

import com.smoothtravel.health.dto.HealthResponse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class HealthServiceTest {

    @Inject
    HealthService healthService;

    @Test
    void shouldReturnOkStatus() {
        HealthResponse response = healthService.getStatus();
        assertEquals("ok", response.status());
    }
}
