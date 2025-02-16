package com.eternalcode.combat.border;

import java.time.Duration;
import org.jetbrains.annotations.ApiStatus;

public interface BorderSettings {

    @ApiStatus.Internal
    default Duration indexRefreshDelay() {
        return Duration.ofSeconds(1);
    }

    double distance();

    default int distanceRounded() {
        return (int) Math.ceil(this.distance());
    }

}
