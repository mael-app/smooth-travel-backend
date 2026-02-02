package com.smoothtravel.infrastructure.redis;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    RedisDataSource dataSource;

    @Mock
    ValueCommands<String, String> valueCommands;

    RedisService redisService;

    @BeforeEach
    void setUp() {
        when(dataSource.value(String.class, String.class)).thenReturn(valueCommands);
        redisService = new RedisService(dataSource);
    }

    @Test
    void shouldSetValue() {
        redisService.set("key", "value");

        verify(valueCommands).set("key", "value");
    }

    @Test
    void shouldSetValueWithTtl() {
        redisService.set("key", "value", Duration.ofMinutes(5));

        verify(valueCommands).setex("key", 300, "value");
    }

    @Test
    void shouldGetValue() {
        when(valueCommands.get("key")).thenReturn("value");

        assertEquals("value", redisService.get("key"));
    }

    @Test
    void shouldReturnNullForMissingKey() {
        when(valueCommands.get("missing")).thenReturn(null);

        assertNull(redisService.get("missing"));
    }

    @Test
    void shouldDeleteKey() {
        redisService.delete("key");

        verify(valueCommands).getdel("key");
    }
}
