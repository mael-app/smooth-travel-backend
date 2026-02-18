package com.smoothtravel.transport.spi.model;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Represents a complete travel itinerary from one stop to another,
 * composed of one or more {@link Leg} segments.
 *
 * @param id            Unique identifier for this journey within the module's namespace.
 * @param origin        Overall departure stop.
 * @param destination   Overall arrival stop.
 * @param departureTime UTC timestamp of the first departure.
 * @param arrivalTime   UTC timestamp of the final arrival.
 * @param duration      Total journey duration.
 * @param transfers     Number of interchanges (connections) in this journey.
 * @param legs          Ordered list of segments constituting this journey.
 * @param moduleId      Identifier of the module that produced this journey.
 */
public record Journey(
        String id,
        Stop origin,
        Stop destination,
        Instant departureTime,
        Instant arrivalTime,
        Duration duration,
        int transfers,
        List<Leg> legs,
        String moduleId
) {
}
