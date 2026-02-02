package com.smoothtravel.infrastructure.redis;

import com.smoothtravel.health.dto.ComponentHealth;
import com.smoothtravel.health.service.HealthIndicator;
import io.vertx.mutiny.redis.client.RedisAPI;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RedisHealthIndicator implements HealthIndicator {

    @Inject
    RedisAPI redisAPI;

    @Override
    public String name() {
        return "redis";
    }

    @Override
    public ComponentHealth check() {
        try {
            redisAPI.ping(java.util.List.of()).await().indefinitely();
            return ComponentHealth.up("Redis connected");
        } catch (Exception e) {
            return ComponentHealth.down(e.getMessage());
        }
    }
}
