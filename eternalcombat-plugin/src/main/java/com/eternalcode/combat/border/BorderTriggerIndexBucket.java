package com.eternalcode.combat.border;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class BorderTriggerIndexBucket {

    private final Map<Long, Set<BorderTrigger>> index = new HashMap<>();

    private BorderTriggerIndexBucket() {
    }

    Set<BorderTrigger> getTriggers(int x, int z) {
        long position = packChunk(x >> 8, z >> 8);
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
        int minX = trigger.min().x() >> 8;
        int minZ = trigger.min().z() >> 8;
        int maxX = trigger.max().x() >> 8;
        int maxZ = trigger.max().z() >> 8;

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

    private static long packChunk(int bigChunkX, int bigChunkZ) {
        return (long) bigChunkX & 0xFFFFFFFFL | ((long) bigChunkZ & 0xFFFFFFFFL) << 32;
    }

}
