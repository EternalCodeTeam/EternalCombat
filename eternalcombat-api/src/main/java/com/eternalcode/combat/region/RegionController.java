package com.eternalcode.combat.region;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class RegionController implements Listener {

    private final NotificationAnnouncer announcer;
    private final RegionProvider regionProvider;
    private final FightManager fightManager;
    private final PluginConfig pluginConfig;
    private final double knockbackMultiplier;

    public RegionController(NotificationAnnouncer announcer, RegionProvider regionProvider, FightManager fightManager, PluginConfig pluginConfig) {
        this.announcer = announcer;
        this.regionProvider = regionProvider;
        this.fightManager = fightManager;
        this.pluginConfig = pluginConfig;

        this.knockbackMultiplier = this.pluginConfig.settings.blockedRegionKnockMultiplier;
    }

    @EventHandler
    void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Location locationTo = event.getTo();
        int xTo = locationTo.getBlockX();
        int yTo = locationTo.getBlockY();
        int zTo = locationTo.getBlockZ();

        Location locationFrom = event.getFrom();
        int xFrom = locationFrom.getBlockX();
        int yFrom = locationFrom.getBlockY();
        int zFrom = locationFrom.getBlockZ();

        if (xTo != xFrom || yTo != yFrom || zTo != zFrom) {
            if (!this.regionProvider.isInRegion(locationTo)) {
                return;
            }

            Location spawnLocation = this.regionProvider.getRegionCenter(locationTo);
            Location playerLocation = player.getLocation();

            double xMultiplier = playerLocation.getX() > spawnLocation.getX() ? 0.5 : -0.5;
            double zMultiplier = playerLocation.getZ() > spawnLocation.getZ() ? 0.5 : -0.5;

            Vector knockback = new Vector(xMultiplier * this.knockbackMultiplier, 0.5, zMultiplier * this.knockbackMultiplier);

            player.setVelocity(knockback);

            this.announcer.sendMessage(player, this.pluginConfig.messages.cantEnterOnRegion);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Location targetLocation = event.getTo();

        if (this.regionProvider.isInRegion(targetLocation)) {
            event.setCancelled(true);
            this.announcer.sendMessage(player, this.pluginConfig.messages.cantEnterOnRegion);
        }
    }
}
