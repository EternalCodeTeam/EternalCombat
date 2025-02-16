package com.eternalcode.combat.border.particle;

import com.eternalcode.combat.border.BorderPoint;
import com.eternalcode.combat.border.BorderService;
import com.eternalcode.combat.border.event.BorderHideAsyncEvent;
import com.eternalcode.combat.border.event.BorderShowAsyncEvent;
import com.eternalcode.commons.scheduler.Scheduler;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.color.Color;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleDustData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BorderParticleController implements Listener {


    public static final PacketEventsAPI<?> PACKET_EVENTS_API = PacketEvents.getAPI();
    public static final PlayerManager PLAYER_MANAGER = PACKET_EVENTS_API.getPlayerManager();
    public static final Vector3f OFFSET = new Vector3f(0.1f, 0.1f, 0.1f);

    private final BorderService borderService;
    private final Server server;
    private final Set<UUID> playersToUpdate = ConcurrentHashMap.newKeySet();

    public BorderParticleController(BorderService borderService, Scheduler scheduler, Server server) {
        this.borderService = borderService;
        this.server = server;
        scheduler.timerAsync(() -> this.updatePlayers(), Duration.ofMillis(200), Duration.ofMillis(200));
    }

    @EventHandler
    void onBorderShowAsyncEvent(BorderShowAsyncEvent event) {
        this.playersToUpdate.add(event.getPlayer().getUniqueId());

        for (BorderPoint point : event.getPoints()) {
            playParticle(event.getPlayer(), point);
        }
    }

    @EventHandler
    void onBorderHideAsyncEvent(BorderHideAsyncEvent event) {
        Set<BorderPoint> border = this.borderService.getActiveBorder(event.getPlayer());
        if (border.isEmpty()) {
            this.playersToUpdate.remove(event.getPlayer().getUniqueId());
        }
    }

    private void updatePlayers() {
        for (UUID uuid : this.playersToUpdate) {
            Player player = this.server.getPlayer(uuid);
            Set<BorderPoint> border = this.borderService.getActiveBorder(player);

            if (border.isEmpty()) {
                this.playersToUpdate.remove(uuid);
                continue;
            }

            for (BorderPoint point : border) {
                playParticle(player, point);
            }
        }
    }

    private void playParticle(Player player, BorderPoint point) {
        Color color = BorderColorUtil.xyzToColor(point.x(), point.y(), point.z());
        Particle<ParticleDustData> dust = new Particle<>(ParticleTypes.DUST, new ParticleDustData(1F, color));
        WrapperPlayServerParticle particle = new WrapperPlayServerParticle(
            dust,
            true,
            new Vector3d(point.x(), point.y(), point.z()),
            OFFSET,
            0.0f,
            5,
            true
        );

        PLAYER_MANAGER.sendPacket(player, particle);
    }

}
