package com.eternalcode.combat.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DurationUtilTest {

    private static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            { "3m 30s", true, Duration.ofMinutes(3).plusSeconds(30).plusMillis(500) },
            { "3m 30.5s", false, Duration.ofMinutes(3).plusSeconds(30).plusMillis(500) },

            { "59m 59s", true, Duration.ofMinutes(59).plusSeconds(59) },
            { "1h 1m", true, Duration.ofHours(1).plusMinutes(1).plusMillis(1) },


            { "0s", true, Duration.ofMillis(500) },
            { "0.5s", false, Duration.ofMillis(500) },
            { "0s", true, Duration.ofMillis(500) },
            { "0s", false, Duration.ZERO },


            { "30s", true, Duration.ofSeconds(30) },
            { "30s", false, Duration.ofSeconds(30) },
            { "30s", true, Duration.ofSeconds(30).plusMillis(500) },
            { "30.5s", false, Duration.ofSeconds(30).plusMillis(500) },

            { "1h 30m 30s", true, Duration.ofHours(1).plusMinutes(30).plusSeconds(30).plusMillis(500) },
            { "1h 30m 30.5s", false, Duration.ofHours(1).plusMinutes(30).plusSeconds(30).plusMillis(500) },
            { "1h 1m 1s", true, Duration.ofHours(1).plusMinutes(1).plusSeconds(1) },
            { "23h 59m 59s", true, Duration.ofHours(23).plusMinutes(59).plusSeconds(59) },
            { "23h 59m 59s", true, Duration.ofHours(23).plusMinutes(59).plusSeconds(59).plusMillis(999) },
            { "26h 3m 4s", true, Duration.ofDays(1).plusHours(2).plusMinutes(3).plusSeconds(4).plusMillis(500) },
            { "1h 1m 1s", true, Duration.ofHours(1).plusMinutes(1).plusSeconds(1).plusMillis(1) },

            { "1h 1s", true, Duration.ofHours(1).plusSeconds(1) },
            { "1m", true, Duration.ofMinutes(1).plusMillis(1) },
        });
    }

    @ParameterizedTest
    @MethodSource("data")
    void testFormat(String expected, boolean removeMillis, Duration duration) {
        String actual = DurationUtil.format(duration, removeMillis);
        assertEquals(expected, actual);
    }
}
