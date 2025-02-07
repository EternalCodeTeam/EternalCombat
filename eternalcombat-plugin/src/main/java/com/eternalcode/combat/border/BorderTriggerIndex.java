package com.eternalcode.combat.border;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class BorderTriggerIndex {

    private final Map<Long, Set<BorderTrigger>> index = new HashMap<>();

    private BorderTriggerIndex() {
    }

    Set<BorderTrigger> getTriggers(int x, int z) {
        long position = packPosition(x, z);
        return this.index.getOrDefault(position, Set.of());
    }

    static BorderTriggerIndex create(Collection<BorderTrigger> triggers) {
        return new BorderTriggerIndex().with(triggers);
    }

    private BorderTriggerIndex with(Collection<BorderTrigger> triggers) {
        for (BorderTrigger trigger : triggers) {
            long position = packPosition(trigger.min().x(), trigger.min().z());
            Set<BorderTrigger> triggersInChunk = this.index.computeIfAbsent(position, key -> new HashSet<>());
            triggersInChunk
                .add(trigger);
        }
        return this;
    }

    private static long packPosition(int x, int z) {
        int bigChunkX = x >> 8;
        int bigChunkZ = z >> 8;
        return (long) bigChunkX & 0xFFFFFFFFL | ((long) bigChunkZ & 0xFFFFFFFFL) << 32;
    }

}
