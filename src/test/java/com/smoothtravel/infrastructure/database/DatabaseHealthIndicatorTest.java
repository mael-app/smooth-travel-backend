package com.smoothtravel.infrastructure.database;

import com.smoothtravel.health.dto.ComponentHealth;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class DatabaseHealthIndicatorTest {

    @Inject
    DatabaseHealthIndicator databaseHealthIndicator;

    @Test
    void shouldReturnCorrectName() {
        String name = databaseHealthIndicator.name();
        assertEquals("database", name);
    }

    @Test
    void shouldCheckDatabaseHealth() {
        ComponentHealth health = databaseHealthIndicator.check();

        assertNotNull(health);
        assertEquals("UP", health.status());
        assertEquals("PostgreSQL connected", health.details());
    }
}
