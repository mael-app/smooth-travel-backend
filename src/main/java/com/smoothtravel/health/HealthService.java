package com.smoothtravel.health;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HealthService {

    public HealthResponse getStatus() {
        return new HealthResponse("ok");
    }
}
