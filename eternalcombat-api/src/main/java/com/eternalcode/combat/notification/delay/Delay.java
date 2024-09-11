package com.eternalcode.combat.notification.delay;

import java.time.Instant;
import java.util.UUID;

public class Delay {

    private final SourceOfDelay source;
    private final Instant end;
    private final UUID uniqueId;

    public Delay(SourceOfDelay source, Instant end, UUID uniqueId) {
        this.source = source;
        this.end = end;
        this.uniqueId = uniqueId;
    }

    public Instant getEnd() {
        return end;
    }

    public SourceOfDelay getSource() {
        return source;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }


}
