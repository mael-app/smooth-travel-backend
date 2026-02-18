package com.smoothtravel.transport.spi.search;

/**
 * Optional filters for a journey search request.
 *
 * <p>Use the nested {@link Builder} to construct instances with default values:
 * <pre>
 *     JourneySearchFilters filters = JourneySearchFilters.builder()
 *             .wheelchairAccessible(true)
 *             .maxTransfers(1)
 *             .build();
 * </pre>
 *
 * @param wheelchairAccessible When {@code true}, only journeys fully accessible to wheelchair
 *                             users are returned. Default: {@code false}.
 * @param bikeAllowed          When {@code true}, only journeys that permit bicycles on every leg
 *                             are returned. Default: {@code false}.
 * @param maxTransfers         Maximum number of transfers allowed. {@code -1} means unlimited.
 *                             Default: {@code -1}.
 * @param arrivalDatetime      When {@code false} (default), {@code datetime} is a departure time.
 *                             When {@code true}, {@code datetime} is a desired arrival time.
 */
public record JourneySearchFilters(
        boolean wheelchairAccessible,
        boolean bikeAllowed,
        int maxTransfers,
        boolean arrivalDatetime
) {

    /** Returns a builder with all defaults applied. */
    public static Builder builder() {
        return new Builder();
    }

    /** Returns a filters instance with all defaults (depart now, no restrictions). */
    public static JourneySearchFilters defaults() {
        return new Builder().build();
    }

    /**
     * Builder for {@link JourneySearchFilters}.
     * All fields are pre-populated with their documented defaults.
     */
    public static final class Builder {

        private boolean wheelchairAccessible = false;
        private boolean bikeAllowed = false;
        private int maxTransfers = -1;
        private boolean arrivalDatetime = false;

        private Builder() {
        }

        public Builder wheelchairAccessible(boolean wheelchairAccessible) {
            this.wheelchairAccessible = wheelchairAccessible;
            return this;
        }

        public Builder bikeAllowed(boolean bikeAllowed) {
            this.bikeAllowed = bikeAllowed;
            return this;
        }

        public Builder maxTransfers(int maxTransfers) {
            this.maxTransfers = maxTransfers;
            return this;
        }

        public Builder arrivalDatetime(boolean arrivalDatetime) {
            this.arrivalDatetime = arrivalDatetime;
            return this;
        }

        public JourneySearchFilters build() {
            return new JourneySearchFilters(
                    wheelchairAccessible,
                    bikeAllowed,
                    maxTransfers,
                    arrivalDatetime
            );
        }
    }
}
