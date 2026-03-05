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
        this.separator = separator;
        this.lastSeparator = lastSeparator;
        this.zero = zero;

        this.tokens = parsePattern(pattern);
    }

    String format(Duration duration) {

        if (duration.isZero() || duration.isNegative()) {
            return zero;
        }

        int count = 0;

        for (Token token : tokens) {
            if (token.unit.extract(duration) > 0) {
                count++;
            }
        }

        if (count == 0) {
            return zero;
        }

        StringBuilder result = new StringBuilder(tokens.length * 16);

        int index = 0;

        for (Token token : tokens) {

            long value = token.unit.extract(duration);
            if (value <= 0) {
                continue;
            }

            if (index > 0) {
                result.append(index == count - 1 ? lastSeparator : separator);
            }

            String word = value == 1 ? token.singular : token.plural;

            if (token.spaceBetween) {
                result.append(value).append(' ').append(word);
            }
            else {
                result.append(value).append(word);
            }

            index++;
        }

        return result.toString();
    }

    private static Token[] parsePattern(String pattern) {

        List<Token> tokens = new ArrayList<>(4);

        for (int i = 0; i < pattern.length(); i++) {

            char c = pattern.charAt(i);
            if (c != '%') {
                continue;
            }

            if (++i >= pattern.length()) {
                throw new IllegalArgumentException("Incomplete placeholder in pattern");
            }

            char symbol = pattern.charAt(i);
            DurationUnit unit = DurationUnit.fromSymbol(symbol);

            int braceIndex = pattern.indexOf('{', i + 1);
            if (braceIndex == -1) {
                throw new IllegalArgumentException("Missing plural definition after %" + symbol);
            }

            boolean spaceBetween = braceIndex > i;

            int start = braceIndex + 1;
            int end = pattern.indexOf('}', start);
            if (end == -1) {
                throw new IllegalArgumentException("Unclosed plural definition in pattern");
            }

            String block = pattern.substring(start, end);

            int split = block.indexOf('|');
            if (split == -1) {
                throw new IllegalArgumentException("Plural must be singular|plural");
            }

            String singular = block.substring(0, split);
            String plural = block.substring(split + 1);

            tokens.add(new Token(unit, singular, plural, spaceBetween));
            i = end;
        }

        return tokens.toArray(new Token[0]);
    }

    private record Token(DurationUnit unit, String singular, String plural, boolean spaceBetween) { }
}
