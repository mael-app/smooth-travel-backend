package com.smoothtravel.infrastructure.redis;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;

@ApplicationScoped
public class RedisService {

    private final ValueCommands<String, String> valueCommands;

    @Inject
    public RedisService(RedisDataSource dataSource) {
        this.valueCommands = dataSource.value(String.class, String.class);
    }

    public void set(String key, String value) {
        valueCommands.set(key, value);
    }

    public void set(String key, String value, Duration ttl) {
        valueCommands.setex(key, ttl.getSeconds(), value);
    }

    public String get(String key) {
        return valueCommands.get(key);
    }

    public void delete(String key) {
        valueCommands.getdel(key);
    }
}
