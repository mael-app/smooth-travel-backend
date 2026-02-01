package com.smoothtravel.health.dto;

import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Health check response")
public record HealthResponse(
        @Schema(description = "Overall service status", examples = {"ok", "degraded"})
        String status,
        @Schema(description = "Individual component health statuses")
        Map<String, ComponentHealth> components
) {
}
