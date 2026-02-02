package com.smoothtravel.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Resend verification code request")
public record ResendCodeRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Schema(description = "User email address", examples = {"user@example.com"})
        String email
) {
}
