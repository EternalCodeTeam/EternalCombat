package com.eternalcode.combat.util;

import java.util.Collection;
import java.util.function.ToIntFunction;

public class MathUtil {

    private MathUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static double getPercentage(int percentage, int baseNumber) {
        return (percentage / 100f) * baseNumber;
    }

    public static int getRoundedPercentage(int percentage, int baseNumber) {
        return (int) Math.round(getPercentage(percentage, baseNumber));
    }

    public static <T> int sum(Collection<T> collection, ToIntFunction<? super T> mapper) {
        return collection.stream().mapToInt(mapper).sum();
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
