package com.eternalcode.combat.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class DurationUtilTest {

    private static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            { Duration.ofMinutes(3).plusSeconds(30).plusMillis(500), true, "3m 30s" },
            { Duration.ofMinutes(3).plusSeconds(30).plusMillis(500), false, "3m 30.5s" },

            { Duration.ofMinutes(59).plusSeconds(59), true, "59m 59s" },
            { Duration.ofHours(1).plusMinutes(1).plusMillis(1), true, "1h 1m" },


            { Duration.ofMillis(500), true, "0s" },
            { Duration.ofMillis(500), false, "0.5s" },
            { Duration.ofMillis(500), true, "0s" },
            { Duration.ZERO, false, "0s" },


            { Duration.ofSeconds(30), true, "30s" },
            { Duration.ofSeconds(30), false, "30s" },
            { Duration.ofSeconds(30).plusMillis(500), true, "30s" },
            { Duration.ofSeconds(30).plusMillis(500), false, "30.5s" },

            { Duration.ofHours(1).plusMinutes(30).plusSeconds(30).plusMillis(500), true, "1h 30m 30s" },
            { Duration.ofHours(1).plusMinutes(30).plusSeconds(30).plusMillis(500), false, "1h 30m 30.5s" },
            { Duration.ofDays(1).plusHours(2).plusMinutes(3).plusSeconds(4).plusMillis(500), true, "26h 3m 4s" },
            { Duration.ofHours(1).plusMinutes(1).plusSeconds(1), true, "1h 1m 1s" },
            { Duration.ofHours(23).plusMinutes(59).plusSeconds(59), true, "23h 59m 59s" },
            { Duration.ofHours(23).plusMinutes(59).plusSeconds(59).plusMillis(999), true, "23h 59m 59s" },
            { Duration.ofHours(1).plusMinutes(1).plusSeconds(1).plusMillis(1), true, "1h 1m 1s" },

            { Duration.ofHours(1).plusSeconds(1), true, "1h 1s" },
            { Duration.ofMinutes(1).plusMillis(1), true, "1m" },
        });
    }

    @ParameterizedTest
    @MethodSource("data")
    void testFormat(Duration duration, boolean removeMillis, String expected) {
        String actual = DurationUtil.format(duration, removeMillis);
        assertEquals(expected, actual);
    }
}
