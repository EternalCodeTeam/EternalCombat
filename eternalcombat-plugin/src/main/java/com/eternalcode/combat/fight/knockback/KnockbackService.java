package com.eternalcode.combat.fight.knockback;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.region.Region;
import com.eternalcode.commons.scheduler.Scheduler;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public final class KnockbackService {

    private final PluginConfig pluginConfig;
    private final Map<UUID, Region> insideRegion = new HashMap<>();
    private final Scheduler scheduler;

    public KnockbackService(PluginConfig pluginConfig, Scheduler scheduler) {
        this.pluginConfig = pluginConfig;
        this.scheduler = scheduler;
    }

    public void konckbackLater(Region region, Player player, Duration duration) {
        this.scheduler.laterSync(() -> knockback(region, player), duration);
    }

    public void forceKnockbackLater(Player player, Region region, Duration duration) {
        if (insideRegion.containsKey(player.getUniqueId())) {
            return;
        }

        insideRegion.put(player.getUniqueId(), region);
        scheduler.laterSync(() -> {
            insideRegion.remove(player.getUniqueId());
            Location playerLocation = player.getLocation();
            if (!region.contains(playerLocation)) {
                return;
            }

            Location location = KnockbackOutsideRegionGenerator.generate(region.getMin(), region.getMax(), playerLocation);
            player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }, duration);
    }

    public void knockback(Region region, Player player) {
        Location centerOfRegion = region.getCenter();
        Location subtract = player.getLocation().subtract(centerOfRegion);

        Vector knockbackVector = new Vector(subtract.getX(), 0, subtract.getZ()).normalize();
        double multiplier = this.pluginConfig.settings.blockedRegionKnockMultiplier;
        Vector configuredVector = new Vector(multiplier, 0.5, multiplier);

        player.setVelocity(knockbackVector.multiply(configuredVector));
    }

}
