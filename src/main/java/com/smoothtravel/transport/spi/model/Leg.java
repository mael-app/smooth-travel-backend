package com.smoothtravel.transport.spi.model;

import java.time.Duration;
import java.time.Instant;

/**
 * A single segment of a {@link Journey} using one transport mode between two stops.
 *
 * @param mode                 The transport mode for this segment.
 * @param origin               Departure stop.
 * @param destination          Arrival stop.
 * @param departureTime        UTC departure timestamp.
 * @param arrivalTime          UTC arrival timestamp.
 * @param duration             Leg duration, always {@code Duration.between(departureTime, arrivalTime)}.
 * @param route                The route used. {@code null} for walking or cycling legs
 *                             ({@link TransportMode#WALK}, {@link TransportMode#BIKE}).
 * @param wheelchairAccessible Whether this leg is accessible to wheelchair users.
 * @param bikeAllowed          Whether bicycles are permitted on this leg.
 */
public record Leg(
        TransportMode mode,
        Stop origin,
        Stop destination,
        Instant departureTime,
        Instant arrivalTime,
        Duration duration,
        Route route,
        boolean wheelchairAccessible,
        boolean bikeAllowed
) {
}
