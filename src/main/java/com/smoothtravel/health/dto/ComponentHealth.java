package com.smoothtravel.health.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Individual component health status")
public record ComponentHealth(
        @Schema(description = "Component status", examples = {"UP", "DOWN"})
        String status,
        @Schema(description = "Additional details")
        String details
) {

    public static ComponentHealth up(String details) {
        return new ComponentHealth("UP", details);
    }

    public static ComponentHealth down(String details) {
        return new ComponentHealth("DOWN", details);
    }
}
