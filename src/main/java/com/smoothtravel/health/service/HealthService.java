package com.smoothtravel.health.service;

import com.smoothtravel.health.dto.ComponentHealth;
import com.smoothtravel.health.dto.HealthResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.LinkedHashMap;
import java.util.Map;

@ApplicationScoped
public class HealthService {

    @Inject
    Instance<HealthIndicator> indicators;

    public HealthResponse getStatus() {
        Map<String, ComponentHealth> components = new LinkedHashMap<>();
        boolean allUp = true;

        for (HealthIndicator indicator : indicators) {
            ComponentHealth health = indicator.check();
            components.put(indicator.name(), health);
            if (!"UP".equals(health.status())) {
                allUp = false;
            }
        }

        String status = allUp ? "ok" : "degraded";
        return new HealthResponse(status, components);
    }
}
