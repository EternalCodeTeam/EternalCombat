package com.eternalcode.combat.fight.trident;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.util.delay.Delay;

import java.time.Duration;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TridentServiceImpl implements TridentService {

    private final FightManager fightManager;
    private final PluginConfig pluginConfig;

    private final Delay<UUID> delay;

    public TridentServiceImpl(FightManager fightManager, PluginConfig pluginConfig) {
        this.fightManager = fightManager;
        this.pluginConfig = pluginConfig;

        this.delay = Delay.withDefault(() -> pluginConfig.trident.tridentRiptideDelay);
    }

    @Override
    public void handleTridentDelay(Player player) {
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        if (this.hasDelay(uniqueId)) {
            return;
        }

        if (this.pluginConfig.trident.tridentRiptideDelay.isZero()) {
            return;
        }

        this.markDelay(uniqueId);
        player.setCooldown(Material.TRIDENT, (int) this.pluginConfig.trident.tridentRiptideDelay.toMillis() / 50);

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
    public Duration getRemainingDelay(UUID uuid) {
        return this.delay.getRemaining(uuid);
    }
}

