package com.smoothtravel.health.resource;

import com.smoothtravel.health.dto.HealthResponse;
import com.smoothtravel.health.service.HealthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HealthResourceTest {

    @Mock
    HealthService healthService;

    @InjectMocks
    HealthResource healthResource;

    @Test
    void shouldReturnHealthStatus() {
        HealthResponse healthResponse = new HealthResponse("UP", Map.of());
        when(healthService.getStatus()).thenReturn(healthResponse);

        HealthResponse result = healthResource.check();

        assertEquals(healthResponse, result);
        verify(healthService).getStatus();
    }
}
