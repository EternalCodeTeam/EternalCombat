package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import javax.annotation.Nullable;
import java.time.Duration;
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

    @EventHandler
    void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player attacked)) {
            return;
        }

        Player enemy = this.getDamager(event);

        if (enemy == null) {
            return;
        }

        Duration combatTime = this.config.settings.combatLogTime;

        UUID attackedUniqueId = attacked.getUniqueId();
        UUID enemyUniqueId = enemy.getUniqueId();

        if (!this.fightManager.isInCombat(attackedUniqueId)) {
            this.announcer.sendMessage(enemy, this.config.messages.tagPlayer);
        }

        if (!this.fightManager.isInCombat(enemyUniqueId)) {
            this.announcer.sendMessage(attacked, this.config.messages.tagPlayer);
        }

        this.fightManager.tag(attackedUniqueId, combatTime);
        this.fightManager.tag(enemyUniqueId, combatTime);
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

}
