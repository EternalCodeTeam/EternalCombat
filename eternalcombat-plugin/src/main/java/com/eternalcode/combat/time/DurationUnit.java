package com.eternalcode.combat.time;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

enum DurationUnit {

    DAYS("d", d -> Math.toIntExact(d.toDaysPart())),
    HOURS("h", Duration::toHoursPart),
    MINUTES("m", Duration::toMinutesPart),
    SECONDS("s", Duration::toSecondsPart),
    MILLIS("ms", d -> d.getSeconds() == 0 ? d.toMillisPart() : 0);

    private static final Map<String, DurationUnit> BY_SYMBOL = Arrays.stream(values())
        .collect(Collectors.toUnmodifiableMap(unit -> unit.symbol, unit -> unit));

    private final String symbol;
    private final Function<Duration, Integer> extractor;

    DurationUnit(String symbol, Function<Duration, Integer> extractor) {
        this.symbol = symbol;
        this.extractor = extractor;
    }

    long getPart(Duration duration) {
        return extractor.apply(duration);
    }

    static DurationUnit fromSymbol(String symbol) {
        DurationUnit unit = BY_SYMBOL.get(symbol);
        if (unit == null) {
            throw new IllegalArgumentException("Unknown duration unit: " + symbol);
        }

        return unit;
    }
}
