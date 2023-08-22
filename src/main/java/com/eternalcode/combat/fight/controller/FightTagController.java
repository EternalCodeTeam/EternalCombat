package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.WhitelistBlacklistMode;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class FightTagController implements Listener {

    private final FightManager fightManager;
    private final PluginConfig config;
    private final NotificationAnnouncer announcer;

    public FightTagController(FightManager fightManager, PluginConfig config, NotificationAnnouncer announcer) {
        this.fightManager = fightManager;
        this.config = config;
        this.announcer = announcer;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player attackedPlayerByPerson)) {
            return;
        }

        if (this.isPlayerInDisabledWorld(attackedPlayerByPerson)) {
            return;
        }

        Player personToAddCombatTime = this.getDamager(event);

        if (personToAddCombatTime == null) {
            return;
        }

        Duration combatTime = this.config.settings.combatDuration;

        UUID attackedUniqueId = attackedPlayerByPerson.getUniqueId();
        UUID personToAddCombatTimeUniqueId = personToAddCombatTime.getUniqueId();

        if (!this.fightManager.isInCombat(attackedUniqueId)) {
            this.announcer.sendMessage(personToAddCombatTime, this.config.messages.playerTagged);
        }

        if (!this.fightManager.isInCombat(personToAddCombatTimeUniqueId)) {
            this.announcer.sendMessage(attackedPlayerByPerson, this.config.messages.playerTagged);
        }

        this.fightManager.tag(attackedUniqueId, combatTime);
        this.fightManager.tag(personToAddCombatTimeUniqueId, combatTime);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onEntityDamage(EntityDamageEvent event) {
        if (!this.config.settings.shouldEnableDamageCauses) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (this.isPlayerInDisabledWorld(player)) {
            return;
        }

        Duration combatTime = this.config.settings.combatDuration;

        UUID uuid = player.getUniqueId();

        List<EntityDamageEvent.DamageCause> damageCauses = this.config.settings.damageCausesToLog;
        WhitelistBlacklistMode mode = this.config.settings.damageCausesMode;

        EntityDamageEvent.DamageCause cause = event.getCause();

        boolean shouldLog = mode.shouldBlock(damageCauses.contains(cause));

        if (shouldLog) {
            return;
        }

        if (!this.fightManager.isInCombat(uuid)) {
            this.announcer.sendMessage(player, this.config.messages.playerTagged);
        }

        this.fightManager.tag(uuid, combatTime);
    }

    @Nullable
    Player getDamager(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager) {
            return damager;
        }

        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) {
            return shooter;
        }

        return null;
    }

    private boolean isPlayerInDisabledWorld(Player player) {
        String worldName = player.getWorld().getName();

        return this.config.settings.worldsToIgnore.contains(worldName);
    }

}
