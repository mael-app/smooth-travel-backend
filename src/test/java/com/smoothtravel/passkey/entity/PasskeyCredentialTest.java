package com.smoothtravel.passkey.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasskeyCredentialTest {

    @Test
    void shouldSetTimestampsOnPrePersist() {
        PasskeyCredential credential = new PasskeyCredential();
        credential.credentialId = new byte[]{1, 2, 3, 4};
        credential.publicKeyCose = new byte[]{5, 6, 7, 8};

        // Simulate @PrePersist call
        credential.onPrePersist();

        assertNotNull(credential.createdAt);
        assertNotNull(credential.updatedAt);
    }

    @Test
    void shouldUpdateTimestampOnPreUpdate() {
        PasskeyCredential credential = new PasskeyCredential();
        credential.credentialId = new byte[]{1, 2, 3, 4};
        credential.publicKeyCose = new byte[]{5, 6, 7, 8};
        credential.createdAt = Instant.now().minusSeconds(3600); // 1 hour ago
        Instant oldUpdatedAt = Instant.now().minusSeconds(1800); // 30 minutes ago
        credential.updatedAt = oldUpdatedAt;

        // Simulate @PreUpdate call
        credential.onPreUpdate();

        assertNotNull(credential.updatedAt);
        assertTrue(credential.updatedAt.isAfter(oldUpdatedAt));
    }
}
