package com.smoothtravel.health;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/health")
public class HealthResource {

    @Inject
    HealthService healthService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HealthResponse check() {
        return healthService.getStatus();
    }
}
