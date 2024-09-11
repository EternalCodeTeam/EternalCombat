package com.eternalcode.combat.notification.delay;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DelayManager {
    private final Map<SourceOfDelay, Map<UUID, Delay>> delayMap = new HashMap<>();

    public void addDelay(UUID uniqueId, SourceOfDelay source, Instant end) {
        Delay delay = new Delay(source, end, uniqueId);
        delayMap.computeIfAbsent(source, k -> new HashMap<>()).put(uniqueId, delay);
    }

    public void removeDelay(UUID uniqueId, SourceOfDelay source) {
        Map<UUID, Delay> sourceMap = delayMap.get(source);
        if (sourceMap != null) {
            sourceMap.remove(uniqueId);
            if (sourceMap.isEmpty()) {
                delayMap.remove(source);
            }
        }
    }

    public boolean hasDelay(UUID uniqueId, SourceOfDelay source) {
        Map<UUID, Delay> sourceMap = delayMap.get(source);
        if (sourceMap != null) {
            Delay delay = sourceMap.get(uniqueId);
            if (delay != null && delay.getEnd().isAfter(Instant.now())) {
                return true;
            }
        }
        return false;
    }
}