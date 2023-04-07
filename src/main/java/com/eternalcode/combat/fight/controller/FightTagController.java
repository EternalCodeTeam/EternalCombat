package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.FightPotionEffectManager;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class FightTagController implements Listener {

    private final FightManager fightManager;
    private final FightPotionEffectManager fightPotionEffectManager;
    private final PluginConfig config;
    private final NotificationAnnouncer announcer;

    public FightTagController(FightManager fightManager, FightPotionEffectManager fightPotionEffectManager, PluginConfig config, NotificationAnnouncer announcer) {
        this.fightManager = fightManager;
        this.fightPotionEffectManager = fightPotionEffectManager;
        this.config = config;
        this.announcer = announcer;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player attackedPlayerByPerson)) {
            return;
        }

        Player personToAddCombatTime = this.getDamager(event);

        if (personToAddCombatTime == null) {
            return;
        }

        Duration combatTime = this.config.settings.combatLogTime;

        UUID attackedUniqueId = attackedPlayerByPerson.getUniqueId();
        UUID personToAddCombatTimeUniqueId = personToAddCombatTime.getUniqueId();

        if (!this.fightManager.isInCombat(attackedUniqueId)) {
            this.announcer.sendMessage(personToAddCombatTime, this.config.messages.tagPlayer);
        }

        if (!this.fightManager.isInCombat(personToAddCombatTimeUniqueId)) {
            this.announcer.sendMessage(attackedPlayerByPerson, this.config.messages.tagPlayer);
        }

        this.fightPotionEffectManager.addPlayerPotionEffects(attackedPlayerByPerson);
        this.fightPotionEffectManager.addPlayerPotionEffects(personToAddCombatTime);

        this.givePotionEffects(attackedPlayerByPerson);
        this.givePotionEffects(personToAddCombatTime);

        this.fightManager.tag(attackedUniqueId, combatTime);
        this.fightManager.tag(personToAddCombatTimeUniqueId, combatTime);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onEntityDamage(EntityDamageEvent event) {
        if (!this.config.settings.enableDamageCauses) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Duration combatTime = this.config.settings.combatLogTime;

        UUID uuid = player.getUniqueId();

        List<EntityDamageEvent.DamageCause> damageCauses = this.config.settings.damageCauses;
        EntityDamageEvent.DamageCause cause = event.getCause();

        if (!damageCauses.contains(cause)) {
            return;
        }

        if (!this.fightManager.isInCombat(uuid)) {
            this.announcer.sendMessage(player, this.config.messages.tagPlayer);
        }

        this.fightPotionEffectManager.addPlayerPotionEffects(player);

        this.givePotionEffects(player);

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

    private void givePotionEffects(Player player) {
        if (!this.config.settings.givePotionEffects) {
            return;
        }

        for (PotionEffect potionEffect : this.config.settings.potionEffects) {
            player.addPotionEffect(potionEffect);
        }
    }

}
