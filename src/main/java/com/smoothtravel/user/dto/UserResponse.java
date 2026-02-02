package com.smoothtravel.user.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "User information")
public record UserResponse(
        @Schema(description = "User unique identifier")
        UUID id,
        @Schema(description = "User email address")
        String email,
        @Schema(description = "Whether the user account is verified")
        boolean verified,
        @Schema(description = "Account creation timestamp")
        Instant createdAt,
        @Schema(description = "Account last update timestamp")
        Instant updatedAt
) {
}
