package com.smoothtravel.health.service;

import com.smoothtravel.health.dto.ComponentHealth;

public interface HealthIndicator {

    String name();

    ComponentHealth check();
}
