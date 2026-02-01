package com.smoothtravel.health.resource;

import com.smoothtravel.health.dto.HealthResponse;
import com.smoothtravel.health.service.HealthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/health")
@Tag(name = "Health", description = "Service health checks")
public class HealthResource {

    @Inject
    HealthService healthService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Check service health", description = "Returns the current status of the API")
    @APIResponse(responseCode = "200", description = "Service is healthy")
    public HealthResponse check() {
        return healthService.getStatus();
    }
}
