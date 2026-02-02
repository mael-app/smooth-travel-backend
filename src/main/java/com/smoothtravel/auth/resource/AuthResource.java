package com.smoothtravel.auth.resource;

import com.smoothtravel.auth.service.TokenBlacklistService;
import com.smoothtravel.auth.service.TokenService;
import com.smoothtravel.user.dto.CreateUserRequest;
import com.smoothtravel.user.dto.LoginRequest;
import com.smoothtravel.user.dto.ResendCodeRequest;
import com.smoothtravel.user.dto.UserResponse;
import com.smoothtravel.user.dto.VerifyCodeRequest;
import com.smoothtravel.user.service.UserService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

@Path("/api/v1/auth")
@Tag(name = "Authentication", description = "Registration, login and logout")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    UserService userService;

    @Inject
    TokenService tokenService;

    @Inject
    TokenBlacklistService tokenBlacklistService;

    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/register")
    @Operation(summary = "Register", description = "Creates a new account and sends a verification code by email")
    @APIResponse(responseCode = "201", description = "Account created, verification code sent")
    @APIResponse(responseCode = "409", description = "Email already verified or verification pending")
    public Response register(@Valid CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return Response.created(URI.create("/api/v1/users/" + user.id()))
                .entity(Map.of("message", "Verification code sent to " + request.email(), "user", user))
                .build();
    }

    @POST
    @Path("/register/verify")
    @Operation(summary = "Verify registration", description = "Verifies email with the 6-character code and returns a JWT")
    @APIResponse(responseCode = "200", description = "Email verified, token returned")
    @APIResponse(responseCode = "400", description = "Invalid or expired code")
    public Response verifyRegistration(@Valid VerifyCodeRequest request) {
        UserResponse user = userService.verifyUser(request.email(), request.code());
        String token = tokenService.generateToken(user.id(), user.email());
        return Response.ok(Map.of("message", "Email verified successfully", "user", user, "token", token)).build();
    }

    @POST
    @Path("/login")
    @Operation(summary = "Login", description = "Sends a verification code to an existing verified account")
    @APIResponse(responseCode = "200", description = "Verification code sent")
    @APIResponse(responseCode = "403", description = "Account not verified")
    @APIResponse(responseCode = "404", description = "Account not found")
    public Response login(@Valid LoginRequest request) {
        userService.loginUser(request.email());
        return Response.ok(Map.of("message", "Verification code sent to " + request.email())).build();
    }

    @POST
    @Path("/login/verify")
    @Operation(summary = "Verify login", description = "Validates the login code and returns a JWT")
    @APIResponse(responseCode = "200", description = "Login successful, token returned")
    @APIResponse(responseCode = "400", description = "Invalid or expired code")
    @APIResponse(responseCode = "403", description = "Account not verified")
    public Response verifyLogin(@Valid VerifyCodeRequest request) {
        UserResponse user = userService.verifyLogin(request.email(), request.code());
        String token = tokenService.generateToken(user.id(), user.email());
        return Response.ok(Map.of("message", "Login successful", "user", user, "token", token)).build();
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
    @Path("/logout")
    @Authenticated
    @Operation(summary = "Logout", description = "Blacklists the current JWT token")
    @APIResponse(responseCode = "200", description = "Logged out successfully")
    @APIResponse(responseCode = "401", description = "Not authenticated")
    public Response logout() {
        String jti = jwt.getTokenID();
        long exp = jwt.getExpirationTime() - Instant.now().getEpochSecond();
        tokenBlacklistService.blacklist(jti, exp);
        return Response.ok(Map.of("message", "Logged out successfully")).build();
    }
}
