package com.eternalcode.combat.fight.death;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.event.CauseOfUnTag;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import java.util.UUID;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathLightningController implements Listener {

    private final PluginConfig pluginConfig;
    private final Server server;

    public DeathLightningController(PluginConfig pluginConfig, Server server) {
        this.pluginConfig = pluginConfig;
        this.server = server;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFightUntagEvent(FightUntagEvent event) {
        CauseOfUnTag cause = event.getCause();
        if (!cause.equals(CauseOfUnTag.DEATH) && !cause.equals(CauseOfUnTag.DEATH_BY_PLAYER)) {
            return;
        }

        UUID uniqueId = event.getPlayer();
        Player player = this.server.getPlayer(uniqueId);

        if (player == null) {
            return;
        }

        if (this.pluginConfig.death.lightning.inCombat && !this.pluginConfig.death.lightning.afterEveryDeath) {
            this.lightningStrike(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeathEventLightning(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (this.pluginConfig.death.lightning.afterEveryDeath) {
            lightningStrike(player);
        }
    }

    private void lightningStrike(Player player) {
        player.getWorld().strikeLightningEffect(player.getLocation());
    }

}
