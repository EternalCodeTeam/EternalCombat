package com.eternalcode.combat.time;

import com.eternalcode.combat.config.implementation.DurationFormatSettings;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public final class DurationService {

    private final DurationFormatter formatter;

    private DurationService(DurationFormatter formatter) {
        this.formatter = formatter;
    }

    public static DurationService ofConfig(@NotNull DurationFormatSettings config) {
        return new DurationService(new DurationFormatter(
                config.pattern,
                config.separator,
                config.lastSeparator,
                config.zero
        ));
    }

    public String format(@NotNull Duration duration) {
        return this.formatter.format(duration);
    }
}
