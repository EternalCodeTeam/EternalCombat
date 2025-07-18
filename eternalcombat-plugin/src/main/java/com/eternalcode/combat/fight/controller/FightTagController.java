package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.WhitelistBlacklistMode;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.CauseOfTag;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
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

    public FightTagController(FightManager fightManager, PluginConfig config) {
        this.fightManager = fightManager;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Thorns are ignored because both users should be in combat from hitting each other
        if (event.getCause().equals(EntityDamageEvent.DamageCause.THORNS)) {
            return;
        }

        if (!(event.getEntity() instanceof Player attackedPlayerByPerson)) {
            return;
        }

        List<EntityType> disabledProjectileEntities = this.config.combat.ignoredProjectileTypes;

        if (event.getDamager() instanceof Projectile projectile && disabledProjectileEntities.contains(projectile.getType())) {
            return;
        }

        if (this.isPlayerInDisabledWorld(attackedPlayerByPerson)) {
            return;
        }

        Player attacker = this.getDamager(event);

        if (attacker == null) {
            return;
        }

        if (this.cannotBeTagged(attacker)) {
            return;
        }

        if (this.cannotBeTagged(attackedPlayerByPerson)) {
            return;
        }

        if (this.config.combat.disableFlying) {
            if (attackedPlayerByPerson.isFlying()) {
                attackedPlayerByPerson.setFlying(false);
                attackedPlayerByPerson.setAllowFlight(false);
            }

            if (attacker.isFlying()) {
                attacker.setFlying(false);
                attacker.setAllowFlight(false);
            }
        }

        Duration combatTime = this.config.settings.combatTimerDuration;
        UUID attackedUniqueId = attackedPlayerByPerson.getUniqueId();
        UUID attackerUniqueId = attacker.getUniqueId();

        this.fightManager.tag(attackedUniqueId, combatTime, CauseOfTag.PLAYER, attackerUniqueId);
        this.fightManager.tag(attackerUniqueId, combatTime, CauseOfTag.PLAYER, attackedUniqueId);
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onEntityDamage(EntityDamageEvent event) {
        if (!this.config.combat.enableDamageCauseLogging) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (this.isPlayerInDisabledWorld(player)) {
            return;
        }

        if (this.cannotBeTagged(player)) {
            return;
        }

        boolean hasBypass = player.hasPermission("eternalcombat.bypass");
        if (hasBypass) {
            return;
        }

        Duration combatTime = this.config.settings.combatTimerDuration;
        UUID uuid = player.getUniqueId();

        List<EntityDamageEvent.DamageCause> damageCauses = this.config.combat.loggedDamageCauses;
        WhitelistBlacklistMode mode = this.config.combat.damageCauseRestrictionMode;

        EntityDamageEvent.DamageCause cause = event.getCause();

        boolean shouldLog = mode.shouldBlock(damageCauses.contains(cause));

        if (shouldLog) {
            return;
        }

        this.fightManager.tag(uuid, combatTime, CauseOfTag.NON_PLAYER);
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

        return this.config.settings.ignoredWorlds.contains(worldName);
    }

    private boolean cannotBeTagged(Player player) {
        if (this.config.admin.excludeAdminsFromCombat && player.hasPermission("eternalcombat.bypass")) {
            return true;
        }

        if (this.config.admin.excludeAdminsFromCombat && player.isOp()) {
            return true;
        }

        return this.config.admin.excludeCreativePlayersFromCombat && player.getGameMode() == GameMode.CREATIVE;
    }



}
