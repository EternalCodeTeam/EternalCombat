package com.eternalcode.combat.fight.death;

import com.eternalcode.combat.config.implementation.PluginConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathEffectController implements Listener {

    private final PluginConfig pluginConfig;

    public DeathEffectController(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    void onPlayerDeathEvent(PlayerDeathEvent event) {
        if (!this.pluginConfig.death.lightning) {
            return;
        }

        Player player = event.getEntity();
        player.getWorld().strikeLightningEffect(player.getLocation());

    }

}
