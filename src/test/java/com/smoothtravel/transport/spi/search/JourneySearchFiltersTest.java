package com.smoothtravel.transport.spi.search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JourneySearchFiltersTest {

    @Test
    void shouldReturnDefaultValues() {
        JourneySearchFilters filters = JourneySearchFilters.defaults();

        assertFalse(filters.wheelchairAccessible());
        assertFalse(filters.bikeAllowed());
        assertEquals(-1, filters.maxTransfers());
        assertFalse(filters.arrivalDatetime());
    }

    @Test
    void shouldBuildWithAllOptions() {
        JourneySearchFilters filters = JourneySearchFilters.builder()
                .wheelchairAccessible(true)
                .bikeAllowed(true)
                .maxTransfers(2)
                .arrivalDatetime(true)
                .build();

        assertTrue(filters.wheelchairAccessible());
        assertTrue(filters.bikeAllowed());
        assertEquals(2, filters.maxTransfers());
        assertTrue(filters.arrivalDatetime());
    }

    @Test
    void shouldBuildWithDefaultsWhenNoOptionSet() {
        JourneySearchFilters filters = JourneySearchFilters.builder().build();

        assertFalse(filters.wheelchairAccessible());
        assertFalse(filters.bikeAllowed());
        assertEquals(-1, filters.maxTransfers());
        assertFalse(filters.arrivalDatetime());
    }

    @Test
    void shouldBuildWithPartialOptions() {
        JourneySearchFilters filters = JourneySearchFilters.builder()
                .wheelchairAccessible(true)
                .maxTransfers(0)
                .build();

        assertTrue(filters.wheelchairAccessible());
        assertFalse(filters.bikeAllowed());
        assertEquals(0, filters.maxTransfers());
        assertFalse(filters.arrivalDatetime());
    }
}
