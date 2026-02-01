package com.smoothtravel.infrastructure.database;

import com.smoothtravel.health.dto.ComponentHealth;
import com.smoothtravel.health.service.HealthIndicator;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.Statement;

@ApplicationScoped
public class DatabaseHealthIndicator implements HealthIndicator {

    @Inject
    AgroalDataSource dataSource;

    @Override
    public String name() {
        return "database";
    }

    @Override
    public ComponentHealth check() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SELECT 1");
            return ComponentHealth.up("PostgreSQL connected");
        } catch (Exception e) {
            return ComponentHealth.down(e.getMessage());
        }
    }
}
