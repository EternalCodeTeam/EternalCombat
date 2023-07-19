package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import panda.utilities.text.Formatter;

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

        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        this.announcer.sendMessage(player, this.config.messages.playerUntagged);
        this.fightManager.untag(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onEntityDamgeByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        if (!(event.getDamager() instanceof Player damager)) {
            return;
        }

        if (victim.getHealth() > event.getFinalDamage()) {
            return;
        }

        if (!this.fightManager.isInCombat(damager.getUniqueId())) {
            return;
        }

        this.fightManager.untag(damager.getUniqueId());
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Formatter formatter = new Formatter()
            .register("{PLAYER}", player.getName());

        String format = formatter.format(this.config.messages.playerLoggedOutDuringCombat);

        this.announcer.broadcast(player, format);

        player.setHealth(0.0); // Untagged in PlayerDeathEvent TODO: move to feature controller (this is not untag action)
    }

}
