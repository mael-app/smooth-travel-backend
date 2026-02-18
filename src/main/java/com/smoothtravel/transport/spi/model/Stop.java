package com.smoothtravel.transport.spi.model;

/**
 * Represents a physical stop or station that a transport module can service.
 *
 * @param id        Unique identifier within the module's own namespace.
 * @param name      Human-readable station or stop name.
 * @param latitude  WGS-84 latitude.
 * @param longitude WGS-84 longitude.
 * @param type      Physical classification of the stop.
 * @param moduleId  Identifier of the {@link com.smoothtravel.transport.spi.TransportModule}
 *                  that produced this stop, e.g. {@code "sncf"} or {@code "tam"}.
 */
public record Stop(
        String id,
        String name,
        double latitude,
        double longitude,
        StopType type,
        String moduleId
) {
}
