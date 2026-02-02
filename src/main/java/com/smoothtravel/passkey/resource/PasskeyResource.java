package com.smoothtravel.passkey.resource;

import com.smoothtravel.auth.service.TokenService;
import com.smoothtravel.passkey.dto.PasskeyAuthStartRequest;
import com.smoothtravel.passkey.dto.PasskeyRegistrationStartRequest;
import com.smoothtravel.passkey.service.PasskeyService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;
import java.util.UUID;

@Path("/api/v1/passkeys")
@Tag(name = "Passkeys", description = "Passkey (WebAuthn) authentication")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PasskeyResource {

    @Inject
    PasskeyService passkeyService;

    @Inject
    TokenService tokenService;

    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/register/start")
    @Authenticated
    @Operation(summary = "Start passkey registration", description = "Generates WebAuthn registration options for the authenticated user")
    @APIResponse(responseCode = "200", description = "Registration options generated")
    @APIResponse(responseCode = "401", description = "Not authenticated")
    @APIResponse(responseCode = "403", description = "User not verified")
    public Response startRegistration(PasskeyRegistrationStartRequest request, @Context SecurityContext ctx) {
        UUID userId = UUID.fromString(jwt.getSubject());
        String options = passkeyService.startRegistration(userId, request != null ? request : new PasskeyRegistrationStartRequest(null));
        return Response.ok(options).type(MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/register/finish")
    @Authenticated
    @Operation(summary = "Finish passkey registration", description = "Completes WebAuthn registration with the authenticator response")
    @APIResponse(responseCode = "200", description = "Passkey registered successfully")
    @APIResponse(responseCode = "400", description = "Registration failed or challenge expired")
    @APIResponse(responseCode = "401", description = "Not authenticated")
    public Response finishRegistration(String body, @Context SecurityContext ctx) {
        UUID userId = UUID.fromString(jwt.getSubject());
        passkeyService.finishRegistration(userId, body);
        return Response.ok(Map.of("message", "Passkey registered successfully")).build();
    }

    @POST
    @Path("/auth/start")
    @Operation(summary = "Start passkey authentication", description = "Generates WebAuthn authentication options")
    @APIResponse(responseCode = "200", description = "Authentication options generated")
    public Response startAuthentication(PasskeyAuthStartRequest request) {
        String options = passkeyService.startAuthentication(request);
        return Response.ok(options).type(MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/auth/finish")
    @Operation(summary = "Finish passkey authentication", description = "Completes WebAuthn authentication and returns a JWT")
    @APIResponse(responseCode = "200", description = "Authentication successful, JWT returned")
    @APIResponse(responseCode = "400", description = "Challenge expired")
    @APIResponse(responseCode = "401", description = "Authentication failed")
    public Response finishAuthentication(String body) {
        try {
            com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(body);
            String requestId = node.get("requestId").asText();
            String credential = node.get("credential").toString();

            PasskeyService.AuthResult result = passkeyService.finishAuthentication(requestId, credential);
            String token = tokenService.generateToken(result.userId(), result.email());

            return Response.ok(Map.of("token", token)).build();
        } catch (com.smoothtravel.passkey.exception.PasskeyAuthenticationException | com.smoothtravel.passkey.exception.PasskeyChallengeExpiredException e) {
            throw e;
        } catch (Exception e) {
            throw new com.smoothtravel.passkey.exception.PasskeyAuthenticationException("Authentication failed: " + e.getMessage());
        }
    }
}
