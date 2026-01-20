package com.eternalcode.combat.protection;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

public class ProtectionServiceImpl implements ProtectionService {

    private final HashMap<UUID, ProtectionTime> protectedPlayers = new HashMap<>();

    @Override
    public void addProtectionTime(UUID uniqueId, Duration duration) {
        ProtectionTime protectionTime = this.protectedPlayers.get(uniqueId);
        if (protectionTime != null) {
            Duration remainingTime = protectionTime.getRemainingTime();
            Instant initialTime = protectionTime.getInitialTime();

            if (initialTime.plus(remainingTime).isBefore(Instant.now().plus(duration))) {
                this.protectedPlayers.put(uniqueId, new ProtectionTime(duration, Instant.now()));
                return;
            }
        }
        protectionTime = new ProtectionTime(duration, Instant.now());
        this.protectedPlayers.put(uniqueId, protectionTime);
    }

    @Override
    public boolean isProtected(UUID uniqueId) {
        ProtectionTime protectionTime = this.protectedPlayers.get(uniqueId);
        if (protectionTime == null) {
            return false;
        }
        if (protectionTime.getInitialTime().plus(protectionTime.getRemainingTime()).isBefore(Instant.now())) {
            this.protectedPlayers.remove(uniqueId);
            return false;
        }
        return true;
    }

    @Override
    public void removeProtection(UUID uniqueId) {
        this.protectedPlayers.remove(uniqueId);
    }
}
