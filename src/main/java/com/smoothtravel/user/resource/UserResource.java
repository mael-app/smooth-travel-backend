package com.smoothtravel.user.resource;

import com.smoothtravel.auth.service.TokenBlacklistService;
import com.smoothtravel.user.service.UserService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.Instant;
import java.util.UUID;

@Path("/api/v1/users")
@Tag(name = "Users", description = "User management")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @Inject
    TokenBlacklistService tokenBlacklistService;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/me")
    @Authenticated
    @Operation(summary = "Get current user", description = "Returns the authenticated user's profile")
    @APIResponse(responseCode = "200", description = "User profile")
    @APIResponse(responseCode = "401", description = "Not authenticated")
    public Response me() {
        return Response.ok(userService.getUserById(UUID.fromString(jwt.getSubject()))).build();
    }

    @DELETE
    @Path("/me")
    @Authenticated
    @Operation(summary = "Delete account", description = "Permanently deletes the authenticated user's account")
    @APIResponse(responseCode = "204", description = "Account deleted")
    @APIResponse(responseCode = "401", description = "Not authenticated")
    public Response deleteAccount() {
        UUID userId = UUID.fromString(jwt.getSubject());
        userService.deleteUser(userId);
        String jti = jwt.getTokenID();
        long exp = jwt.getExpirationTime() - Instant.now().getEpochSecond();
        tokenBlacklistService.blacklist(jti, exp);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get user by ID", description = "Returns a user by their unique identifier")
    @APIResponse(responseCode = "200", description = "User found")
    @APIResponse(responseCode = "404", description = "User not found")
    public Response getUserById(@PathParam("id") UUID id) {
        return userService.getUserById(id)
                .map(user -> Response.ok(user).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}
