package com.eternalcode.combat.border;

import com.eternalcode.commons.scheduler.Scheduler;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleDustData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class BorderParticleController implements Listener {

    public static final PacketEventsAPI<?> PACKET_EVENTS_API = PacketEvents.getAPI();
    public static final PlayerManager PLAYER_MANAGER = PACKET_EVENTS_API.getPlayerManager();
    public static final ClientVersion SERVER_VERSION = PACKET_EVENTS_API.getServerManager().getVersion().toClientVersion();

    private static final WrappedBlockState GLASS_STATE = WrappedBlockState.getDefaultState(SERVER_VERSION, StateTypes.RED_STAINED_GLASS);

    private final BorderService borderService;
    private final Scheduler scheduler;

    public BorderParticleController(BorderService borderService, Scheduler scheduler) {
        this.borderService = borderService;
        this.scheduler = scheduler;
    }

    @EventHandler
    void onMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        Location from = event.getFrom();
        if (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()) {
            return;
        }


        Optional<BorderResult> borderPoints = borderService.resolveBorder(to);

        if (borderPoints.isPresent()) {
            BorderResult result = borderPoints.get();
            scheduler.async(() -> {
                List<BorderPoint> results = result.parallelStream()
                    .toList();

                for (BorderPoint borderPoint : results) {
                    playParticle(event.getPlayer(), borderPoint);
                }

                playBlock(event.getPlayer(), results);
            });
        }

    }

    public void playBlock(Player player, List<BorderPoint> blocks) {
        Map<Vector3i, Set<BorderPoint>> chunksToUpdate = new HashMap<>();
        for (BorderPoint block : blocks) {
            Vector3i chunk = new Vector3i(
                block.x() >> 4,
                block.y() >> 4,
                block.z() >> 4
            );

            chunksToUpdate.computeIfAbsent(chunk, k -> new HashSet<>()).add(block);
        }

        for (Map.Entry<Vector3i, Set<BorderPoint>> entry : chunksToUpdate.entrySet()) {
            Vector3i chunk = entry.getKey();
            WrapperPlayServerMultiBlockChange.EncodedBlock[] encodedBlocks = entry.getValue().stream()
                .map(borderPoint -> this.toEncodedBlock(borderPoint))
                .toArray(value -> new WrapperPlayServerMultiBlockChange.EncodedBlock[value]);

            WrapperPlayServerMultiBlockChange multiBlockChange = new WrapperPlayServerMultiBlockChange(chunk,
                true,
                encodedBlocks
            );

            PLAYER_MANAGER.sendPacket(player, multiBlockChange);
        }
    }

    private WrapperPlayServerMultiBlockChange.EncodedBlock toEncodedBlock(BorderPoint point) {
        return new WrapperPlayServerMultiBlockChange.EncodedBlock(GLASS_STATE.getGlobalId(), point.x(), point.y(), point.z());
    }

    public void playParticle(Player player, BorderPoint point) {
        Particle<ParticleDustData> dust = new Particle<>(
            ParticleTypes.DUST,
            new ParticleDustData(1F, xyzToRGB(point.x(), point.y(), point.z()))
        );

        WrapperPlayServerParticle particle = new WrapperPlayServerParticle(
            dust,
            true,
            new Vector3d(
                point.x(),
                point.y(),
                point.z()
            ),
            new Vector3f(0.0f, 0.0f, 0.0f),
            0.0f,
            1,
            true
        );

        PLAYER_MANAGER.sendPacket(player, particle);
    }

    public static com.github.retrooper.packetevents.protocol.color.Color xyzToRGB(int x, int y, int z) {
        float hue = (float) (((Math.sin(x * 0.05) + Math.cos(z * 0.05)) * 0.5 + 0.5) % 1.0);
        float saturation = 1.0f;
        float brightness = 0.8f + 0.2f * Math.max(0.0f, Math.min(1.0f, (float) y / 255));

        java.awt.Color hsbColor = java.awt.Color.getHSBColor(hue, saturation, brightness);
        return new com.github.retrooper.packetevents.protocol.color.Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue());
    }

}
