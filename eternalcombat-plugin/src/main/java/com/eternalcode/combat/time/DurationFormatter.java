package com.eternalcode.combat.time;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

final class DurationFormatter {

    private final Token[] tokens;
    private final String separator;
    private final String lastSeparator;
    private final String zero;

    DurationFormatter(
        @NotNull String pattern,
        @NotNull String separator,
        @NotNull String lastSeparator,
        @NotNull String zero
    ) {
        this.tokens = parsePattern(pattern);
        this.separator = separator;
        this.lastSeparator = lastSeparator;
        this.zero = zero;
    }

    String format(Duration duration) {
        if (duration.isZero() || duration.isNegative()) {
            return zero;
        }

        long[] values = new long[tokens.length];
        int count = 0;

        for (int i = 0; i < tokens.length; i++) {
            long value = tokens[i].unit.extract(duration);
            values[i] = value;

            if (value > 0) {
                count++;
            }
        }

        if (count == 0) {
            return zero;
        }

        StringBuilder result = new StringBuilder(tokens.length * 16);
        int index = 0;

        for (int i = 0; i < tokens.length; i++) {
            long value = values[i];
            if (value <= 0) {
                continue;
            }

            Token token = tokens[i];

            if (index++ > 0) {
                result.append(index == count ? lastSeparator : separator);
            }

            result.append(value);

            if (token.spaceBetween) {
                result.append(' ');
            }

            result.append(value == 1 ? token.singular : token.plural);
        }

        return result.toString();
    }

    private static Token[] parsePattern(String pattern) {
        List<Token> tokens = new ArrayList<>(DurationUnit.values().length);

        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) != '%') {
                continue;
            }

            int start = ++i;
            while (i < pattern.length() && Character.isLetter(pattern.charAt(i))) {
                i++;
            }

            if (start == i) {
                throw new IllegalArgumentException("Missing duration unit after %");
            }

            if (i >= pattern.length()) {
                throw new IllegalArgumentException("Unexpected end of pattern");
            }

            DurationUnit unit = DurationUnit.fromSymbol(pattern.substring(start, i));

            boolean spaceBetween = pattern.charAt(i) == ' ';
            if (spaceBetween) {
                i++;
            }

            if (pattern.charAt(i) != '{') {
                throw new IllegalArgumentException("Missing plural definition");
            }

            int blockStart = ++i;
            while (pattern.charAt(i) != '}') {
                i++;
            }

            String block = pattern.substring(blockStart, i);
            int split = block.indexOf('|');
            if (split == -1) {
                throw new IllegalArgumentException("Plural must be singular|plural");
            }

            tokens.add(new Token(
                unit,
                block.substring(0, split),
                block.substring(split + 1),
                spaceBetween
            ));
        }

        return tokens.toArray(new Token[0]);
    }

    private record Token(
        DurationUnit unit,
        String singular,
        String plural,
        boolean spaceBetween
    ) {}
}
