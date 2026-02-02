package com.smoothtravel.user.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void shouldSetTimestampsOnPrePersist() {
        User user = new User();
        user.email = "test@example.com";
        user.verified = false;

        // Simulate @PrePersist call
        user.onPrePersist();

        assertNotNull(user.createdAt);
        assertNotNull(user.updatedAt);
    }

    @Test
    void shouldUpdateTimestampOnPreUpdate() {
        User user = new User();
        user.email = "test@example.com";
        user.verified = false;
        user.createdAt = Instant.now().minusSeconds(3600); // 1 hour ago
        Instant oldUpdatedAt = Instant.now().minusSeconds(1800); // 30 minutes ago
        user.updatedAt = oldUpdatedAt;

        // Simulate @PreUpdate call
        user.onPreUpdate();

        assertNotNull(user.updatedAt);
        assertTrue(user.updatedAt.isAfter(oldUpdatedAt));
    }
}
