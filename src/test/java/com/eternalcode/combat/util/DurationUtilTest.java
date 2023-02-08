package com.eternalcode.combat.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DurationUtilTest {

    private static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { Duration.ofMinutes(3).plusSeconds(30).plusMillis(500), true, "3m 30s" },
            { Duration.ofMinutes(3).plusSeconds(30).plusMillis(500), false, "3m 30.5s" },
            { Duration.ofMillis(500), true, "0s" },
            { Duration.ofMillis(500), false, "0.5s" },
            { Duration.ofSeconds(30).plusMillis(500), true, "30s" },
            { Duration.ofSeconds(30).plusMillis(500), false, "30.5s" },
            { Duration.ofHours(1).plusMinutes(30).plusSeconds(30).plusMillis(500), true, "1h 30m 30s" },
            { Duration.ofHours(1).plusMinutes(30).plusSeconds(30).plusMillis(500), false, "1h 30m 30.5s" },
            { Duration.ZERO, false, "0s" }
        });
    }

    @ParameterizedTest
    @MethodSource("data")
    void testFormat(Duration duration, boolean removeMillis, String expected) {
        String actual = DurationUtil.format(duration, removeMillis);
        assertEquals(expected, actual);
    }
}
