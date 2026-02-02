package com.smoothtravel.user.resource;

import com.smoothtravel.user.dto.CreateUserRequest;
import com.smoothtravel.user.dto.ResendCodeRequest;
import com.smoothtravel.user.dto.UserResponse;
import com.smoothtravel.user.dto.VerifyCodeRequest;
import com.smoothtravel.user.service.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Path("/api/v1/users")
@Tag(name = "Users", description = "User management")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @POST
    @Operation(summary = "Create a new user", description = "Registers a new user and sends a verification code by email")
    @APIResponse(responseCode = "201", description = "User created, verification code sent")
    @APIResponse(responseCode = "409", description = "Email already verified or verification pending")
    public Response createUser(@Valid CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return Response.created(URI.create("/api/v1/users/" + user.id()))
                .entity(Map.of("message", "Verification code sent to " + request.email(), "user", user))
                .build();
    }

    @POST
    @Path("/resend-code")
    @Operation(summary = "Resend verification code", description = "Resends a verification code (30s cooldown)")
    @APIResponse(responseCode = "200", description = "Verification code resent")
    @APIResponse(responseCode = "429", description = "Must wait before resending")
    public Response resendCode(@Valid ResendCodeRequest request) {
        userService.resendVerificationCode(request.email());
        return Response.ok(Map.of("message", "Verification code sent to " + request.email())).build();
    }

    @POST
    @Path("/verify")
    @Operation(summary = "Verify email", description = "Verifies a user email with the 6-character code")
    @APIResponse(responseCode = "200", description = "Email verified successfully")
    @APIResponse(responseCode = "400", description = "Invalid or expired code")
    public Response verifyEmail(@Valid VerifyCodeRequest request) {
        UserResponse user = userService.verifyUser(request.email(), request.code());
        return Response.ok(Map.of("message", "Email verified successfully", "user", user)).build();
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
