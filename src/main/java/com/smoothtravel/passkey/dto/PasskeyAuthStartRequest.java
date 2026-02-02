package com.smoothtravel.passkey.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Passkey authentication start request")
public record PasskeyAuthStartRequest(
        @Schema(description = "User email (optional, for non-discoverable credentials)")
        String email
) {
}
