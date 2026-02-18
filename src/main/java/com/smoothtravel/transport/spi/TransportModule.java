package com.smoothtravel.transport.spi;

import com.smoothtravel.transport.spi.model.Journey;
import com.smoothtravel.transport.spi.model.Stop;
import com.smoothtravel.transport.spi.search.JourneySearchFilters;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service Provider Interface (SPI) for transport network modules.
 *
 * <p>Each transport provider (e.g. SNCF, TAM Montpellier) must implement this interface
 * and register itself as a CDI bean (typically {@code @ApplicationScoped}).
 * The {@link com.smoothtravel.transport.spi.registry.TransportModuleRegistry} will
 * discover all implementations automatically via {@code Instance<TransportModule>}.
 *
 * <h2>Package Convention</h2>
 * <p>Concrete implementations must live in
 * {@code com.smoothtravel.transport.provider.{id}} (e.g.
 * {@code com.smoothtravel.transport.provider.sncf}).
 *
 * <h2>Identifier Convention</h2>
 * <p>{@link #getId()} must return a stable lowercase ASCII slug, e.g. {@code "sncf"} or
 * {@code "tam"}. This value is embedded in {@link com.smoothtravel.transport.spi.model.Stop}
 * and {@link com.smoothtravel.transport.spi.model.Journey} records to allow consumers
 * to route follow-up requests back to the correct module.
 *
 * <h2>Configuration Convention</h2>
 * <p>Implementations must read their enabled flag from MicroProfile Config using the key
 * {@code app.transport.{id}.enabled}, where {@code {id}} is the value returned by
 * {@link #getId()}. Example for the SNCF module:
 * <pre>
 *     {@code @ConfigProperty(name = "app.transport.sncf.enabled", defaultValue = "true")}
 *     boolean enabled;
 * </pre>
 *
 * <h2>Exception Contract</h2>
 * <p>All implementations must throw
 * {@link com.smoothtravel.transport.spi.exception.TransportModuleException} (or a subclass)
 * when an unrecoverable error occurs during a remote API call. Never let provider-specific
 * checked exceptions propagate through this interface.
 */
public interface TransportModule {

    /**
     * Returns the stable, unique identifier of this module.
     * Must be a lowercase ASCII slug, e.g. {@code "sncf"}, {@code "tam"}.
     *
     * @return unique module id
     */
    String getId();

    /**
     * Returns the human-readable display name of this module.
     *
     * @return display name, e.g. {@code "SNCF"}, {@code "TAM Montpellier"}
     */
    String getName();

    /**
     * Returns whether this module is currently enabled.
     *
     * <p>Implementations should read the value from
     * {@code app.transport.{id}.enabled} via {@code @ConfigProperty}.
     *
     * @return {@code true} if the module should participate in journey searches
     */
    boolean isEnabled();

    /**
     * Searches for journeys between two stops.
     *
     * @param fromStopId stop identifier in this module's namespace
     * @param toStopId   stop identifier in this module's namespace
     * @param datetime   reference time (departure or arrival, controlled by
     *                   {@link JourneySearchFilters#arrivalDatetime()})
     * @param filters    optional search constraints; never {@code null}
     *                   (use {@link JourneySearchFilters#defaults()} when no filters apply)
     * @return ordered list of journeys, empty if none found; never {@code null}
     * @throws com.smoothtravel.transport.spi.exception.TransportModuleException
     *         if the underlying provider returns an error or is unreachable
     */
    List<Journey> searchJourneys(String fromStopId,
                                 String toStopId,
                                 Instant datetime,
                                 JourneySearchFilters filters);

    /**
     * Searches for stops matching a free-text query.
     *
     * @param query autocomplete input, e.g. {@code "Montpellier"}
     * @param limit maximum number of results to return
     * @return matching stops, empty list if none; never {@code null}
     * @throws com.smoothtravel.transport.spi.exception.TransportModuleException
     *         if the underlying provider is unreachable
     */
    List<Stop> searchStops(String query, int limit);

    /**
     * Finds a single stop by its identifier.
     *
     * @param stopId stop identifier in this module's namespace
     * @return the stop, or {@link Optional#empty()} if not found
     * @throws com.smoothtravel.transport.spi.exception.TransportModuleException
     *         if the underlying provider is unreachable
     */
    Optional<Stop> findStop(String stopId);
}
