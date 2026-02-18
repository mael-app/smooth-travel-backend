package com.smoothtravel.transport.spi.model;

/**
 * Modes of transport supported by the SmoothTravel platform.
 * Used to characterise {@link Leg} segments within a {@link Journey}.
 */
public enum TransportMode {
    TRAIN,
    TGV,
    INTERCITES,
    METRO,
    BUS,
    TRAM,
    FERRY,
    RER,
    WALK,
    BIKE
}
