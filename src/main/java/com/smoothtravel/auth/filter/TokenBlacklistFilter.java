package com.smoothtravel.auth.filter;

import com.smoothtravel.auth.service.TokenBlacklistService;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Map;

@Provider
@Priority(Priorities.AUTHENTICATION + 1)
public class TokenBlacklistFilter implements ContainerRequestFilter {

    @Inject
    JsonWebToken jwt;

    @Inject
    TokenBlacklistService tokenBlacklistService;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String jti = jwt.getTokenID();
        if (jti != null && tokenBlacklistService.isBlacklisted(jti)) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .type(MediaType.APPLICATION_JSON)
                            .entity(Map.of("error", "Token has been revoked"))
                            .build()
            );
        }
    }
}
