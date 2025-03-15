package com.eternalcode.combat.border;

import org.jetbrains.annotations.Nullable;

public record BorderPoint(int x, int y, int z, @Nullable BorderPoint inclusive) {

    public BorderPoint(int x, int y, int z) {
        this(x, y, z, null);
    }

    public BorderPoint toInclusive() {
        if (inclusive == null) {
            return this;
        }

        return inclusive;
    }

}
