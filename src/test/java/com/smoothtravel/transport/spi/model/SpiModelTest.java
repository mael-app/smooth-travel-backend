package com.smoothtravel.transport.spi.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpiModelTest {

    @Test
    void shouldCreateStop() {
        Stop stop = new Stop("ST001", "Paris Gare de Lyon", 48.8448, 2.3738, StopType.TRAIN_STATION, "sncf");

        assertEquals("ST001", stop.id());
        assertEquals("Paris Gare de Lyon", stop.name());
        assertEquals(48.8448, stop.latitude());
        assertEquals(2.3738, stop.longitude());
        assertEquals(StopType.TRAIN_STATION, stop.type());
        assertEquals("sncf", stop.moduleId());
    }

    @Test
    void shouldCreateRoute() {
        Route route = new Route("R001", "TGV Paris-Montpellier", TransportMode.TGV, "TGV 6201", "#E2001A");

        assertEquals("R001", route.id());
        assertEquals("TGV Paris-Montpellier", route.name());
        assertEquals(TransportMode.TGV, route.mode());
        assertEquals("TGV 6201", route.shortName());
        assertEquals("#E2001A", route.color());
    }

    @Test
    void shouldCreateRouteWithNullColor() {
        Route route = new Route("R002", "Bus line 3", TransportMode.BUS, "3", null);

        assertNull(route.color());
    }

    @Test
    void shouldCreateLeg() {
        Stop origin = new Stop("S1", "Paris Gare de Lyon", 48.8448, 2.3738, StopType.TRAIN_STATION, "sncf");
        Stop destination = new Stop("S2", "Montpellier Saint-Roch", 43.6047, 3.8797, StopType.TRAIN_STATION, "sncf");
        Route route = new Route("R1", "TGV Paris-Montpellier", TransportMode.TGV, "TGV 6201", null);
        Instant departure = Instant.now();
        Instant arrival = departure.plusSeconds(12600);
        Duration duration = Duration.ofMinutes(210);

        Leg leg = new Leg(TransportMode.TGV, origin, destination, departure, arrival, duration, route, true, false);

        assertEquals(TransportMode.TGV, leg.mode());
        assertEquals(origin, leg.origin());
        assertEquals(destination, leg.destination());
        assertEquals(departure, leg.departureTime());
        assertEquals(arrival, leg.arrivalTime());
        assertEquals(duration, leg.duration());
        assertEquals(route, leg.route());
        assertTrue(leg.wheelchairAccessible());
        assertFalse(leg.bikeAllowed());
    }

    @Test
    void shouldCreateWalkingLegWithNullRoute() {
        Stop origin = new Stop("S1", "Montpellier Saint-Roch", 43.6047, 3.8797, StopType.TRAIN_STATION, "sncf");
        Stop destination = new Stop("S2", "Montpellier Saint-Roch — Sortie Rue Maguelone", 43.6051, 3.8800, StopType.TRAM_STOP, "tam");
        Instant departure = Instant.now();
        Duration duration = Duration.ofMinutes(3);

        Leg walkLeg = new Leg(TransportMode.WALK, origin, destination,
                departure, departure.plusSeconds(180), duration, null, true, false);

        assertEquals(TransportMode.WALK, walkLeg.mode());
        assertNull(walkLeg.route());
    }

    @Test
    void shouldCreateJourney() {
        Stop origin = new Stop("S1", "Paris Gare de Lyon", 48.8448, 2.3738, StopType.TRAIN_STATION, "sncf");
        Stop destination = new Stop("S2", "Montpellier Saint-Roch", 43.6047, 3.8797, StopType.TRAIN_STATION, "sncf");
        Instant departure = Instant.now();
        Instant arrival = departure.plusSeconds(12600);
        Duration duration = Duration.ofMinutes(210);
        List<Leg> legs = List.of();

        Journey journey = new Journey("J001", origin, destination, departure, arrival, duration, 0, legs, "sncf");

        assertEquals("J001", journey.id());
        assertEquals(origin, journey.origin());
        assertEquals(destination, journey.destination());
        assertEquals(departure, journey.departureTime());
        assertEquals(arrival, journey.arrivalTime());
        assertEquals(duration, journey.duration());
        assertEquals(0, journey.transfers());
        assertEquals(legs, journey.legs());
        assertEquals("sncf", journey.moduleId());
    }

    @Test
    void shouldCoverAllTransportModes() {
        assertEquals(10, TransportMode.values().length);
        assertNotNull(TransportMode.valueOf("TRAIN"));
        assertNotNull(TransportMode.valueOf("TGV"));
        assertNotNull(TransportMode.valueOf("INTERCITES"));
        assertNotNull(TransportMode.valueOf("METRO"));
        assertNotNull(TransportMode.valueOf("BUS"));
        assertNotNull(TransportMode.valueOf("TRAM"));
        assertNotNull(TransportMode.valueOf("FERRY"));
        assertNotNull(TransportMode.valueOf("RER"));
        assertNotNull(TransportMode.valueOf("WALK"));
        assertNotNull(TransportMode.valueOf("BIKE"));
    }

    @Test
    void shouldCoverAllStopTypes() {
        assertEquals(6, StopType.values().length);
        assertNotNull(StopType.valueOf("TRAIN_STATION"));
        assertNotNull(StopType.valueOf("METRO_STATION"));
        assertNotNull(StopType.valueOf("BUS_STOP"));
        assertNotNull(StopType.valueOf("TRAM_STOP"));
        assertNotNull(StopType.valueOf("PORT"));
        assertNotNull(StopType.valueOf("AIRPORT"));
    }
}
