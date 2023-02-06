package com.eternalcode.combat.combat.controller;

import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.combat.CombatManager;
import com.eternalcode.combat.config.implementation.PluginConfig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.UUID;

public class CombatTagController implements Listener {

    private final CombatManager combatManager;
    private final PluginConfig config;
    private final NotificationAnnouncer notificationAnnouncer;

    public CombatTagController(CombatManager combatManager, PluginConfig config, NotificationAnnouncer notificationAnnouncer) {
        this.combatManager = combatManager;
        this.config = config;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
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

        this.combatManager.tag(attackedUniqueId, combatTime);
        this.combatManager.tag(enemyUniqueId, combatTime);

        this.notificationAnnouncer.sendMessage(attacked, this.config.messages.tagPlayer);
        this.notificationAnnouncer.sendMessage(enemy, this.config.messages.tagPlayer);
    }

    private @Nullable Player getDamager(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager) {
            return damager;
        }


        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) {
            return shooter;
        }

        return null;
    }

}
