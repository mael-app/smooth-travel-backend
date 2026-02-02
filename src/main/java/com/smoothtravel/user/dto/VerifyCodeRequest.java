package com.smoothtravel.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Verify email code request")
public record VerifyCodeRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Schema(description = "User email address")
        String email,

        @NotBlank(message = "Code is required")
        @Size(min = 6, max = 6, message = "Code must be 6 characters")
        @Schema(description = "6-character verification code")
        String code
) {
}
