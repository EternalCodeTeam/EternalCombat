package com.eternalcode.combat.time;

import java.time.Duration;

enum DurationUnit {

    DAYS('d') {
        @Override
        long extract(Duration duration) {
            return duration.toDaysPart();
        }
    },

    HOURS('h') {
        @Override
        long extract(Duration duration) {
            return duration.toHoursPart();
        }
    },

    MINUTES('m') {
        @Override
        long extract(Duration duration) {
            return duration.toMinutesPart();
        }
    },

    SECONDS('s') {
        @Override
        long extract(Duration duration) {
            return duration.toSecondsPart();
        }
    };

    private static final DurationUnit[] VALUES = values();

    private final char symbol;

    DurationUnit(char symbol) {
        this.symbol = symbol;
    }

    abstract long extract(Duration duration);

    static DurationUnit fromSymbol(char symbol) {
        for (DurationUnit unit : VALUES) {
            if (unit.symbol == symbol) {
                return unit;
            }
        }

        throw new IllegalArgumentException("Unknown duration unit: %" + symbol);
    }
}
