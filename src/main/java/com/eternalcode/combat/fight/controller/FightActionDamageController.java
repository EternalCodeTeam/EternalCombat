package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class FightActionDamageController implements Listener {

    private final FightManager fightManager;
    private final PluginConfig pluginConfig;
    private final NotificationAnnouncer announcer;

    public FightActionDamageController(FightManager fightManager, PluginConfig pluginConfig, NotificationAnnouncer announcer) {
        this.fightManager = fightManager;
        this.pluginConfig = pluginConfig;
        this.announcer = announcer;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onEntityDamage(EntityDamageEvent event) {
        if (!this.pluginConfig.settings.enableDamageCauses) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Duration combatTime = this.pluginConfig.settings.combatLogTime;

        UUID uuid = player.getUniqueId();

        List<EntityDamageEvent.DamageCause> damageCauses = this.pluginConfig.settings.damageCauses;
        EntityDamageEvent.DamageCause cause = event.getCause();

        if (!damageCauses.contains(cause)) {
            return;
        }

        if (!this.fightManager.isInCombat(uuid)) {
            this.announcer.sendMessage(player, this.pluginConfig.messages.tagPlayer);
        }

        this.fightManager.tag(uuid, combatTime);
    }

}
