package com.pragma.plazoleta.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TimeConfigurationTest {

    @Test
    @DisplayName("Should create Clock bean configured with America/Bogota timezone")
    void shouldCreateClockBean() {
        TimeConfiguration timeConfiguration = new TimeConfiguration();
        Clock clock = timeConfiguration.clock();

        assertNotNull(clock);
        assertEquals(ZoneId.of("America/Bogota"), clock.getZone());
    }
}