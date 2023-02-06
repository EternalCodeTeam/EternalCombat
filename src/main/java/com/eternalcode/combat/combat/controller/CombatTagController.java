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
    private final NotificationAnnouncer announcer;

    public CombatTagController(CombatManager combatManager, PluginConfig config, NotificationAnnouncer announcer) {
        this.combatManager = combatManager;
        this.config = config;
        this.announcer = announcer;
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

        this.announcer.sendMessage(attacked, this.config.messages.tagPlayer);
        this.announcer.sendMessage(enemy, this.config.messages.tagPlayer);
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
