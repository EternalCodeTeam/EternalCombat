package com.eternalcode.combat.border;

import org.jetbrains.annotations.Nullable;

public record BorderPoint(int x, int y, int z, @Nullable BorderPoint innerPoint) {

    public BorderPoint(int x, int y, int z) {
        this(x, y, z, null);
    }

    /**
     * This point represents an adjusted coordinate that takes into account the block's volume (thickness)
     * and is used for logical placement and grouping, especially for assigning border blocks to the
     * correct chunk when they are on the very edge of a chunk boundary.
     * This ensures the block is considered to be on the "inner" side of the border for chunk-based logic.
     */
    @Override
    public BorderPoint innerPoint() {
        if (this.innerPoint == null) {
            return this;
        }

        return this.innerPoint;
    }
}
