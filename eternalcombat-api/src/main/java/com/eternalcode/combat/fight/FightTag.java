package com.eternalcode.combat.fight;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class FightTag {

    private final UUID taggedPlayer;
    private final Instant endOfCombatLog;
    private final @Nullable UUID tagger;

    FightTag(UUID personToAddCombat, Instant endOfCombatLog, @Nullable UUID tagger) {
        this.taggedPlayer = personToAddCombat;
        this.endOfCombatLog = endOfCombatLog;
        this.tagger = tagger;
    }

    public UUID getTaggedPlayer() {
        return this.taggedPlayer;
    }

    public Instant getEndOfCombatLog() {
        return this.endOfCombatLog;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.endOfCombatLog);
    }

    public Duration getRemainingDuration() {
        Duration between = Duration.between(Instant.now(), this.endOfCombatLog);

        if (between.isNegative()) {
            return Duration.ZERO;
        }

        return between;
    }

    @Nullable
    @ApiStatus.Experimental
    public UUID getTagger() {
        return this.tagger;
    }

}
