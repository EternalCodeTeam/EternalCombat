package com.eternalcode.combat.util;

import com.eternalcode.commons.time.DurationParser;
import com.eternalcode.commons.time.TemporalAmountParser;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public final class DurationUtil {

    private static final TemporalAmountParser<Duration> WITHOUT_MILLS = new DurationParser()
        .withUnit("s", ChronoUnit.SECONDS)
        .withUnit("m", ChronoUnit.MINUTES)
        .withUnit("h", ChronoUnit.HOURS)
        .withUnit("d", ChronoUnit.DAYS)
        .roundOff(ChronoUnit.MILLIS);

    public DurationUtil() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static String format(Duration duration) {
        return WITHOUT_MILLS.format(duration);
    }
}
