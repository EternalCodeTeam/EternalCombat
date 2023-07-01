package com.eternalcode.combat.config.composer;

import dev.rollczi.litecommands.shared.EstimatedTemporalAmountParser;
import panda.std.Result;

import java.time.Duration;

public class DurationComposer implements SimpleComposer<Duration> {

    @Override
    public Result<Duration, Exception> deserialize(String source) {
        if (source.isEmpty() || source.equals("0")) {
            return Result.ok(Duration.ZERO);
        }

        return Result.supplyThrowing(IllegalArgumentException.class, () -> EstimatedTemporalAmountParser.TIME_UNITS.parse(source));
    }

    @Override
    public Result<String, Exception> serialize(Duration entity) {
        return Result.ok(EstimatedTemporalAmountParser.TIME_UNITS.format(entity));
    }

}
