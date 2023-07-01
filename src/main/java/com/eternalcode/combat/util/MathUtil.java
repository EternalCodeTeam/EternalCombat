package com.eternalcode.combat.util;

public class MathUtil {

    private MathUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static double calculatePercentageValue(int percentage, int baseNumber) {
        return (percentage / 100f) * baseNumber;
    }

    public static long calculateRoundedPercentageValue(int percentage, int baseNumber) {
        return Math.round(calculatePercentageValue(percentage, baseNumber));
    }

}
