package com.eternalcode.combat.time;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

enum DurationUnit {

    DAYS("d") {
        @Override
        long extract(Duration duration) {
            return duration.toDaysPart();
        }
    },

    HOURS("h") {
        @Override
        long extract(Duration duration) {
            return duration.toHoursPart();
        }
    },

    MINUTES("m") {
        @Override
        long extract(Duration duration) {
            return duration.toMinutesPart();
        }
    },

    SECONDS("s") {
        @Override
        long extract(Duration duration) {
            return duration.toSecondsPart();
        }
    },

    MILLIS("ms") {
        @Override
        long extract(Duration duration) {
            return duration.getSeconds() == 0
                ? duration.toMillisPart()
                : 0;
        }
    };

    private static final Map<String, DurationUnit> BY_SYMBOL = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(unit -> unit.symbol, unit -> unit));

    private final String symbol;

    DurationUnit(String symbol) {
        this.symbol = symbol;
    }

    abstract long extract(Duration duration);

    static DurationUnit fromSymbol(String symbol) {
        DurationUnit unit = BY_SYMBOL.get(symbol);

        if (unit == null) {
            throw new IllegalArgumentException("Unknown duration unit: %" + symbol);
        }

        return unit;
    }
}
