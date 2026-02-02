package com.smoothtravel.infrastructure.redis;

import com.smoothtravel.health.dto.ComponentHealth;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.redis.client.RedisAPI;
import io.vertx.mutiny.redis.client.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisHealthIndicatorTest {

    @Mock
    RedisAPI redisAPI;

    @Mock
    Response response;

    @InjectMocks
    RedisHealthIndicator redisHealthIndicator;

    @Test
    void shouldReturnUpWhenRedisIsReachable() {
        when(redisAPI.ping(List.of())).thenReturn(Uni.createFrom().item(response));

        ComponentHealth health = redisHealthIndicator.check();

        assertEquals("UP", health.status());
        assertEquals("Redis connected", health.details());
    }

    @Test
    void shouldReturnDownWhenRedisIsUnreachable() {
        when(redisAPI.ping(List.of())).thenReturn(Uni.createFrom().failure(new RuntimeException("Connection refused")));

        ComponentHealth health = redisHealthIndicator.check();

        assertEquals("DOWN", health.status());
        assertEquals("Connection refused", health.details());
    }

    @Test
    void shouldReturnCorrectName() {
        assertEquals("redis", redisHealthIndicator.name());
    }
}
