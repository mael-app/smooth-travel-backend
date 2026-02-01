package com.smoothtravel.health.service;

import com.smoothtravel.health.dto.HealthResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HealthService {

    public HealthResponse getStatus() {
        return new HealthResponse("ok");
    }
}
