package com.eternalcode.combat.fight.blocker;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ElytraBlocker implements Listener {

    private final FightManager fightManager;
    private final PluginConfig config;
    private final Cache<UUID, Boolean> fallProtection = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(10))
        .build();

    public ElytraBlocker(FightManager fightManager, PluginConfig config) {
        this.fightManager = fightManager;
        this.config = config;
    }

    @EventHandler
    void onToggleGlide(EntityToggleGlideEvent event) {
        if (!this.config.combat.disableElytraUsage) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        if (event.isGliding()) {
            event.setCancelled(true);
            player.setGliding(false);
        }
    }


    @EventHandler
    void onMoveWhileGliding(PlayerMoveEvent event) {
        if (!this.config.combat.disableElytraUsage) {
            return;
        }

        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        if (player.isGliding()) {
            player.setGliding(false);

            player.setVelocity(player.getVelocity().setY(-10));
            player.setFallDistance(0f);
            fallProtection.put(uniqueId, true);

        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        if (fallProtection.get(player.getUniqueId(), key -> false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onDamage(EntityDamageEvent event) {
        if (!this.config.combat.disableElytraOnDamage) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        UUID uniqueId = player.getUniqueId();

        if (this.fightManager.isInCombat(uniqueId) && player.isGliding()) {
            player.setGliding(false);
        }
    }

}
