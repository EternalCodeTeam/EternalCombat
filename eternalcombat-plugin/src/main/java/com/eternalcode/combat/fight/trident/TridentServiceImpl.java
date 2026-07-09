package com.eternalcode.combat.fight.trident;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.commons.delay.Delay;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.UUID;

public class TridentServiceImpl implements TridentService {

    private final Delay<UUID> delay;

    public TridentServiceImpl(PluginConfig pluginConfig) {
        this.delay = Delay.withDefault(() -> pluginConfig.trident.tridentRiptideDelay);
    }


    @Override
    public void markDelay(UUID uuid) {
        this.delay.markDelay(uuid);
    }

    @Override
    public boolean hasDelay(UUID uuid) {
        return this.delay.hasDelay(uuid);
    }

    @Override
    public void removeDelay(UUID playerId) {
        this.delay.unmarkDelay(playerId);
    }

    @Override
    public Duration getRemainingDelay(UUID uuid) {
        return this.delay.getRemaining(uuid);
    }
}

