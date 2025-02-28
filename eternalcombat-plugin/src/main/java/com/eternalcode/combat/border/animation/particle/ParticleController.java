package com.eternalcode.combat.border.animation.particle;

import com.eternalcode.combat.border.BorderPoint;
import com.eternalcode.combat.border.BorderService;
import com.eternalcode.combat.border.event.BorderHideAsyncEvent;
import com.eternalcode.combat.border.event.BorderShowAsyncEvent;
import com.eternalcode.commons.scheduler.Scheduler;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ParticleController implements Listener {

    public static final PacketEventsAPI<?> PACKET_EVENTS_API = PacketEvents.getAPI();
    public static final PlayerManager PLAYER_MANAGER = PACKET_EVENTS_API.getPlayerManager();

    private final BorderService borderService;
    private final ParticleSettings particleSettings;
    private final Server server;
    private final Set<UUID> playersToUpdate = ConcurrentHashMap.newKeySet();

    public ParticleController(BorderService borderService, ParticleSettings particleSettings, Scheduler scheduler, Server server) {
        this.borderService = borderService;
        this.particleSettings = particleSettings;
        this.server = server;
        scheduler.timerAsync(() -> this.updatePlayers(), Duration.ofMillis(200), Duration.ofMillis(200));
    }

    @EventHandler
    void onBorderShowAsyncEvent(BorderShowAsyncEvent event) {
        if (!particleSettings.enabled) {
            return;
        }

        this.playersToUpdate.add(event.getPlayer().getUniqueId());

        for (BorderPoint point : event.getPoints()) {
            this.playParticle(event.getPlayer(), point);
        }
    }

    @EventHandler
    void onBorderHideAsyncEvent(BorderHideAsyncEvent event) {
        if (!particleSettings.enabled) {
            return;
        }

        Set<BorderPoint> border = this.borderService.getActiveBorder(event.getPlayer());
        if (border.isEmpty()) {
            this.playersToUpdate.remove(event.getPlayer().getUniqueId());
        }
    }

    private void updatePlayers() {
        if (!particleSettings.enabled) {
            return;
        }

        for (UUID uuid : this.playersToUpdate) {
            Player player = this.server.getPlayer(uuid);
            Set<BorderPoint> border = this.borderService.getActiveBorder(player);

            if (border.isEmpty()) {
                this.playersToUpdate.remove(uuid);
                continue;
            }

            for (BorderPoint point : border) {
                this.playParticle(player, point);
            }
        }
    }

    private void playParticle(Player player, BorderPoint point) {
        WrapperPlayServerParticle particle = particleSettings.getParticle(point);

        PLAYER_MANAGER.sendPacket(player, particle);
    }

}
