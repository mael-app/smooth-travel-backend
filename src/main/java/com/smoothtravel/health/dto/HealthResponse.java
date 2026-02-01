package com.smoothtravel.health.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Health check response")
public record HealthResponse(
        @Schema(description = "Service status", examples = {"ok"})
        String status
) {
}
