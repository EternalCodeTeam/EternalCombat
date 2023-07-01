package com.eternalcode.combat.util;

public class MathUtil {

    private MathUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static double getFromPercent(int percentage, int fromNumber) {
        return (percentage / 100f) * fromNumber;
    }

    public static long getRoundedFromPercent(int percentage, int fromNumber) {
        return Math.round(getFromPercent(percentage, fromNumber));
    }
}
