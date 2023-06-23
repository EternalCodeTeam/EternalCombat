package com.eternalcode.combat.region;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class RegionController implements Listener {

    private final NotificationAnnouncer announcer;
    private final RegionService regionService;
    private final FightManager fightManager;
    private final PluginConfig pluginConfig;

    public RegionController(NotificationAnnouncer announcer, RegionService regionService, FightManager fightManager, PluginConfig pluginConfig) {
        this.announcer = announcer;
        this.regionService = regionService;
        this.fightManager = fightManager;
        this.pluginConfig = pluginConfig;
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
            if (!this.regionService.isInCombatRegion(locationTo, this.pluginConfig.settings.blockedRegions)) {
                return;
            }

            Location playerLocation = player.getLocation().subtract(player.getWorld().getSpawnLocation());
            double distance = playerLocation.distance(player.getWorld().getSpawnLocation());
            Vector knockback = new Vector(0, 3, 0).multiply(this.pluginConfig.settings.blockedRegionMultiply / distance);
            Vector vector = playerLocation.toVector().add(knockback);

            player.setVelocity(vector);

            this.announcer.sendMessage(player, this.pluginConfig.messages.cantEnterOnRegion);
        }
    }
}
