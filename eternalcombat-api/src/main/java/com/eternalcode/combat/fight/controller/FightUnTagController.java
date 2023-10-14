package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.fight.event.CauseOfUnTag;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class FightUnTagController implements Listener {

    private final FightManager fightManager;
    private final PluginConfig config;

    public FightUnTagController(FightManager fightManager, PluginConfig config) {
        this.fightManager = fightManager;
        this.config = config;
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        CauseOfUnTag causeOfUnTag = (killer == null ? CauseOfUnTag.DEATH_BY_PLAYER : CauseOfUnTag.DEATH);

        this.fightManager.untag(player.getUniqueId(), causeOfUnTag);

        if (killer != null && this.config.settings.shouldReleaseAttacker) {
            this.fightManager.untag(killer.getUniqueId(), CauseOfUnTag.ATTACKER_RELEASE);
        }
    }
}
