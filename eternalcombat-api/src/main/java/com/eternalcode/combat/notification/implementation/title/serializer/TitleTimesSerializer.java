package com.eternalcode.combat.notification.implementation.title.serializer;

import dev.rollczi.litecommands.shared.EstimatedTemporalAmountParser;
import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.kyori.adventure.title.Title;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;

public class TitleTimesSerializer extends BidirectionalTransformer<String, Title.Times> {

    private static final String TITLE_TIMES_FORMAT = "%s %s %s";

    private static final EstimatedTemporalAmountParser<Duration> DURATION_PARSER = EstimatedTemporalAmountParser.TIME_UNITS;
    
    @Override
    public GenericsPair<String, Title.Times> getPair() {
        return this.genericsPair(String.class, Title.Times.class);
    }

    @Override
    public Title.Times leftToRight(@NonNull String data, @NonNull SerdesContext serdesContext) {
        String[] arguments = data.split(" ");

        Duration fadeIn = DURATION_PARSER.parse(arguments[0]);
        Duration stay = DURATION_PARSER.parse(arguments[1]);
        Duration fadeOut = DURATION_PARSER.parse(arguments[2]);

        return Title.Times.times(fadeIn, stay, fadeOut);
    }

    @Override
    public String rightToLeft(Title.Times times, @NonNull SerdesContext serdesContext) {
        String fadeIn = DURATION_PARSER.format(times.fadeIn());
        String stay = DURATION_PARSER.format(times.stay());
        String fadeOut = DURATION_PARSER.format(times.fadeOut());

        return TITLE_TIMES_FORMAT.formatted(fadeIn, stay, fadeOut);
    }
}
