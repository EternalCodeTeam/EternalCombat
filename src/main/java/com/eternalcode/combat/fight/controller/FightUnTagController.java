package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class FightUnTagController implements Listener {

    private final FightManager fightManager;
    private final PluginConfig config;
    private final NotificationAnnouncer announcer;

    public FightUnTagController(FightManager fightManager, PluginConfig config, NotificationAnnouncer announcer) {
        this.fightManager = fightManager;
        this.config = config;
        this.announcer = announcer;
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        this.announcer.sendMessage(player, this.config.messages.playerUntagged);
        this.fightManager.untag(player.getUniqueId());

        if (killer != null && this.config.settings.shouldReleaseAttacker) {
            this.fightManager.untag(killer.getUniqueId());
            this.announcer.sendMessage(killer, this.config.messages.playerUntagged);
        }
    }
}
