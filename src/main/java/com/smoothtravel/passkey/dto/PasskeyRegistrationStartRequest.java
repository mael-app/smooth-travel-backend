package com.smoothtravel.passkey.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Passkey registration start request")
public record PasskeyRegistrationStartRequest(
        @Schema(description = "Optional display name for the passkey")
        String displayName
) {
}
