package com.eternalcode.combat.fight.blocker;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.FightTagEvent;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class FlyingBlocker implements Listener {

    private final FightManager fightManager;
    private final PluginConfig config;
    private final Server server;

    public FlyingBlocker(FightManager fightManager, PluginConfig config, Server server) {
        this.fightManager = fightManager;
        this.config = config;
        this.server = server;
    }


    @EventHandler
    void onFly(PlayerToggleFlightEvent event) {
        if (!this.config.combat.disableFlying) {
            return;
        }

        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        if (event.isFlying()) {
            player.setAllowFlight(false);

            event.setCancelled(true);
        }
    }


    @EventHandler
    void onTag(FightTagEvent event) {
        UUID uniqueId = event.getPlayer();
        Player player = this.server.getPlayer(uniqueId);

        if (player == null) {
            return;
        }

        if (this.config.combat.disableFlying) {
            GameMode gameMode = player.getGameMode();

            if (gameMode != GameMode.CREATIVE && gameMode != GameMode.SPECTATOR) {
                player.setAllowFlight(false);
                player.setFlying(false);
            }
        }
    }

    @EventHandler
    void onUnTag(FightUntagEvent event) {
        if (!this.config.combat.disableFlying) {
            return;
        }

        UUID uniqueId = event.getPlayer();
        Player player = this.server.getPlayer(uniqueId);

        if (player == null) {
            return;
        }
        GameMode playerGameMode = player.getGameMode();
        if (playerGameMode == GameMode.CREATIVE || playerGameMode == GameMode.SPECTATOR) {
            player.setAllowFlight(true);
        }
    }

}
