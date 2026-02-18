package com.smoothtravel.transport.spi.model;

/**
 * Represents a named transport route (e.g. TGV line, bus line 3).
 *
 * @param id        Unique identifier within the module's namespace.
 * @param name      Full route name.
 * @param mode      Transport mode of this route.
 * @param shortName Short identifier, e.g. line number or letter ("3", "B", "TGV 6201").
 * @param color     Hex colour code for UI display, e.g. {@code "#E2001A"}.
 *                  May be {@code null} if the provider does not supply one.
 */
public record Route(
        String id,
        String name,
        TransportMode mode,
        String shortName,
        String color
) {
}
