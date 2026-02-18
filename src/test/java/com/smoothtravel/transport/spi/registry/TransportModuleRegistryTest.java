package com.smoothtravel.transport.spi.registry;

import com.smoothtravel.transport.spi.TransportModule;
import jakarta.enterprise.inject.Instance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransportModuleRegistryTest {

    @Mock
    @SuppressWarnings("unchecked")
    Instance<TransportModule> modules;

    @InjectMocks
    TransportModuleRegistry registry;

    private TransportModule enabledModule;
    private TransportModule disabledModule;

    @BeforeEach
    void setUp() {
        enabledModule = mock(TransportModule.class);
        when(enabledModule.getId()).thenReturn("sncf");
        when(enabledModule.getName()).thenReturn("SNCF");
        when(enabledModule.isEnabled()).thenReturn(true);

        disabledModule = mock(TransportModule.class);
        when(disabledModule.getId()).thenReturn("tam");
        when(disabledModule.getName()).thenReturn("TAM Montpellier");
        when(disabledModule.isEnabled()).thenReturn(false);

        when(modules.spliterator())
                .thenReturn(List.of(enabledModule, disabledModule).spliterator());
    }

    @Test
    void shouldReturnOnlyEnabledModules() {
        List<TransportModule> result = registry.getEnabledModules();

        assertEquals(1, result.size());
        assertEquals("sncf", result.get(0).getId());
        assertTrue(result.get(0).isEnabled());
    }

    @Test
    void shouldReturnAllModulesIncludingDisabled() {
        List<TransportModule> result = registry.getAllModules();

        assertEquals(2, result.size());
    }

    @Test
    void shouldFindEnabledModuleById() {
        Optional<TransportModule> result = registry.findById("sncf");

        assertTrue(result.isPresent());
        assertEquals("sncf", result.get().getId());
    }

    @Test
    void shouldFindDisabledModuleById() {
        Optional<TransportModule> result = registry.findById("tam");

        assertTrue(result.isPresent());
        assertEquals("tam", result.get().getId());
        assertFalse(result.get().isEnabled());
    }

    @Test
    void shouldReturnEmptyForUnknownModuleId() {
        Optional<TransportModule> result = registry.findById("unknown");

        assertTrue(result.isEmpty());
    }
}
