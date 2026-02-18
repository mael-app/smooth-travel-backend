package com.smoothtravel.transport.spi.registry;

import com.smoothtravel.transport.spi.TransportModule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Central registry for all {@link TransportModule} implementations discovered via CDI.
 *
 * <p>New transport providers are registered automatically: implement {@link TransportModule},
 * annotate with {@code @ApplicationScoped}, and the registry will include them at startup.
 *
 * <p>Modules that have {@link TransportModule#isEnabled()} returning {@code false} are visible
 * via {@link #getAllModules()} but excluded from {@link #getEnabledModules()}.
 */
@ApplicationScoped
public class TransportModuleRegistry {

    @Inject
    Instance<TransportModule> modules;

    /**
     * Returns all modules whose {@link TransportModule#isEnabled()} returns {@code true}.
     *
     * @return immutable list of enabled modules; empty if none are enabled
     */
    public List<TransportModule> getEnabledModules() {
        return StreamSupport.stream(modules.spliterator(), false)
                .filter(TransportModule::isEnabled)
                .toList();
    }

    /**
     * Returns all registered modules regardless of their enabled state.
     *
     * @return immutable list of all modules
     */
    public List<TransportModule> getAllModules() {
        return StreamSupport.stream(modules.spliterator(), false)
                .toList();
    }

    /**
     * Finds a module by its unique identifier.
     *
     * <p>Searches across all modules, including disabled ones, so callers can distinguish
     * between "module exists but is disabled" and "unknown module id".
     *
     * @param id the module id to look up, e.g. {@code "sncf"}
     * @return the matching module, or {@link Optional#empty()} if not found
     */
    public Optional<TransportModule> findById(String id) {
        return StreamSupport.stream(modules.spliterator(), false)
                .filter(m -> m.getId().equals(id))
                .findFirst();
    }
}
