package com.eternalcode.combat.border;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class BorderTriggerIndexBucket {

    /**
     * Represents "00000000 00000000 00000000 00000000 11111111 11111111 11111111 11111111" bit mask.
     * This allows removing left bits when used with AND bit operator.
     * */
    private static final long LEFT_INT_MASK = 0xFFFFFFFFL;

    /**
     * Allows packing the coordination on one axis from 256 into 1
     * Why? - We divide the world into fragments of size 256x256
     * Why it is 8? Because when you shift bits, then the value will be smaller / bigger in scale 2^n
     * E.g `2^4 = 16` (standard minecraft chunk size) in our case, it is `2^8 = 256`
     */
    private static final int CHUNK_SHIFT = 8;

    private final Map<Long, Set<BorderTrigger>> index = new HashMap<>();

    private BorderTriggerIndexBucket() {
    }

    Set<BorderTrigger> getTriggers(int x, int z) {
        long position = packChunk(x >> CHUNK_SHIFT, z >> CHUNK_SHIFT);
        return this.index.getOrDefault(position, Set.of());
    }

    static BorderTriggerIndexBucket create(Collection<BorderTrigger> triggers) {
        return new BorderTriggerIndexBucket().with(triggers);
    }

    private BorderTriggerIndexBucket with(Collection<BorderTrigger> triggers) {
        for (BorderTrigger trigger : triggers) {
            withTrigger(trigger);
        }
        return this;
    }

    private void withTrigger(BorderTrigger trigger) {
        int minX = trigger.min().x() >> CHUNK_SHIFT;
        int minZ = trigger.min().z() >> CHUNK_SHIFT;
        int maxX = trigger.max().x() >> CHUNK_SHIFT;
        int maxZ = trigger.max().z() >> CHUNK_SHIFT;

        int startX = Math.min(minX, maxX);
        int startZ = Math.min(minZ, maxZ);
        int endX = Math.max(minX, maxX);
        int endZ = Math.max(minZ, maxZ);

        for (int chunkX = startX; chunkX <= endX; chunkX++) {
            for (int chunkZ = startZ; chunkZ <= endZ; chunkZ++) {
                long packed = packChunk(chunkX, chunkZ);
                this.index.computeIfAbsent(packed, key -> new HashSet<>())
                    .add(trigger);
            }
        }
    }

    /**
     * Packs two ints into long value.
     * <p>
     * For example for values:
     * <ul>
     *   <li>X - 00000000 00000000 00000000 00000001 </li>
     *   <li>Z - 00000000 00000000 00000000 00000111 </li>
     * </ul>
     * This method will return:<br>
     * 00000000 00000000 00000000 00000111 00000000 00000000 00000000 00000001
     * </p>
     * @param bigChunkX right int to pack
     * @param bigChunkZ left int to pack
     */
    private static long packChunk(int bigChunkX, int bigChunkZ) {
        return (long) bigChunkX & LEFT_INT_MASK | ((long) bigChunkZ & LEFT_INT_MASK) << Integer.SIZE;
    }

}
