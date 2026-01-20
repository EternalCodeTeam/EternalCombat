package com.eternalcode.combat.protection;

import java.time.Duration;
import java.util.UUID;

public interface ProtectionService {

    void addProtectionTime(UUID uniqueId, Duration duration);

    boolean isProtected(UUID uniqueId);

    void removeProtection(UUID uniqueId);

}
