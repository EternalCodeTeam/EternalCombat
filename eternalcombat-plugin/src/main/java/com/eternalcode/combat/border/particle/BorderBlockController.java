package com.eternalcode.combat.border.particle;

import com.eternalcode.combat.border.BorderPoint;
import com.eternalcode.combat.border.BorderService;
import com.eternalcode.combat.border.event.BorderHideAsyncEvent;
import com.eternalcode.combat.border.event.BorderShowAsyncEvent;
import com.eternalcode.commons.scheduler.Scheduler;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange.EncodedBlock;
import io.papermc.lib.PaperLib;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChunkSnapshot;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BorderBlockController implements Listener {

    public static final PacketEventsAPI<?> PACKET_EVENTS_API = PacketEvents.getAPI();
    public static final PlayerManager PLAYER_MANAGER = PACKET_EVENTS_API.getPlayerManager();
    public static final ClientVersion SERVER_VERSION = PACKET_EVENTS_API.getServerManager().getVersion().toClientVersion();
    public static final int AIR_ID = WrappedBlockState.getDefaultState(SERVER_VERSION, StateTypes.AIR).getGlobalId();

    private final BorderService borderService;
    private final Server server;
    private final Set<UUID> playersToUpdate = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Object> lockedPlayers = new ConcurrentHashMap<>();

    public BorderBlockController(BorderService borderService, Scheduler scheduler, Server server) {
        this.borderService = borderService;
        this.server = server;
        scheduler.timerAsync(() -> this.updatePlayers(), Duration.ofMillis(200), Duration.ofMillis(200));
    }

    @EventHandler
    void onBorderShowAsyncEvent(BorderShowAsyncEvent event) {
        Player player = event.getPlayer();
        Set<BorderPoint> borderPoints = processChunks(player, event.getPoints());

        event.setPoints(borderPoints);
        this.playBlocks(player, borderPoints);
        this.playersToUpdate.add(player.getUniqueId());
    }

    @EventHandler
    void onBorderHideAsyncEvent(BorderHideAsyncEvent event) {
        Object lock = lockedPlayers.computeIfAbsent(event.getPlayer().getUniqueId(), k -> new Object());
        synchronized (lock) {
            this.playHideBlocks(event.getPlayer(), event.getPoints());

            Set<BorderPoint> border = this.borderService.getActiveBorder(event.getPlayer());
            if (border.isEmpty()) {
                this.playersToUpdate.remove(event.getPlayer().getUniqueId());
            }
        }
    }

    private void updatePlayers() {
        for (UUID uuid : this.playersToUpdate) {
            Player player = this.server.getPlayer(uuid);
            if (player == null) {
                this.playersToUpdate.remove(uuid);
                continue;
            }

            updatePlayer(uuid, player);
        }
    }

    private void updatePlayer(UUID uuid, Player player) {
        Object lock = lockedPlayers.computeIfAbsent(uuid, k -> new Object());
        synchronized (lock) {
            Set<BorderPoint> border = this.borderService.getActiveBorder(player);

            if (border.isEmpty()) {
                this.playersToUpdate.remove(uuid);
                return;
            }

            playBlocks(player, border);
        }
    }

    record ChunkLocation(int x, int z) {}

    public void playBlocks(Player player, Collection<BorderPoint> blocks) {
        Map<Vector3i, Set<BorderPoint>> chunksToUpdate = splitToChunks(blocks);

        for (Map.Entry<Vector3i, Set<BorderPoint>> entry : chunksToUpdate.entrySet()) {
            Vector3i chunk = entry.getKey();
            EncodedBlock[] encodedBlocks = entry.getValue().stream()
                .map(borderPoint -> this.toEncodedBlock(borderPoint))
                .toArray(value -> new EncodedBlock[value]);

            WrapperPlayServerMultiBlockChange multiBlockChange = new WrapperPlayServerMultiBlockChange(chunk,
                true,
                encodedBlocks
            );

            PLAYER_MANAGER.sendPacket(player, multiBlockChange);
        }
    }

    public void playHideBlocks(Player player, Collection<BorderPoint> blocks) {
        Map<Vector3i, Set<BorderPoint>> chunksToUpdate = splitToChunks(blocks);

        for (Map.Entry<Vector3i, Set<BorderPoint>> entry : chunksToUpdate.entrySet()) {
            Vector3i chunk = entry.getKey();
            EncodedBlock[] encodedBlocks = entry.getValue().stream()
                .map(point -> new EncodedBlock(AIR_ID, point.x(), point.y(), point.z()))
                .toArray(value -> new EncodedBlock[value]);

            WrapperPlayServerMultiBlockChange multiBlockChange = new WrapperPlayServerMultiBlockChange(chunk,
                true,
                encodedBlocks
            );

            PLAYER_MANAGER.sendPacket(player, multiBlockChange);
        }
    }

    private static Set<BorderPoint> processChunks(Player player, Collection<BorderPoint> blocks) {
        Map<ChunkLocation, Set<BorderPoint>> chunksToProcess = new HashMap<>();
        for (BorderPoint block : blocks) {
            BorderPoint inclusive = block.toInclusive();
            ChunkLocation location = new ChunkLocation(inclusive.x() >> 4, inclusive.z() >> 4);
            chunksToProcess.computeIfAbsent(location, k -> new HashSet<>()).add(inclusive);
        }

        Set<BorderPoint> points = new HashSet<>();
        chunksToProcess.forEach((location, chunkPoints) -> {
            ChunkSnapshot chunk = PaperLib.getChunkAtAsync(player.getWorld(), location.x(), location.z(), false)
                .thenApply(chunk1 -> chunk1.getChunkSnapshot())
                .join();

            for (BorderPoint point : chunkPoints) {
                Material type = chunk.getBlockType(point.x() - (location.x() << 4), point.y(), point.z() - (location.z() << 4));
                if (!type.isAir()) {
                    continue;
                }

                points.add(point);
            }
        });

        return points;
    }

    private Map<Vector3i, Set<BorderPoint>> splitToChunks(Collection<BorderPoint> blocks) {
        Map<Vector3i, Set<BorderPoint>> chunksToUpdate = new HashMap<>();
        for (BorderPoint block : blocks) {
            Vector3i chunk = new Vector3i(
                block.x() >> 4,
                block.y() >> 4,
                block.z() >> 4
            );

            chunksToUpdate.computeIfAbsent(chunk, k -> new HashSet<>()).add(block);
        }
        return chunksToUpdate;
    }


    private EncodedBlock toEncodedBlock(BorderPoint point) {
        StateType type = xyzToRGB(point.x(), point.y(), point.z());
        WrappedBlockState state = WrappedBlockState.getDefaultState(SERVER_VERSION, type);
        return new EncodedBlock(state.getGlobalId(), point.x(), point.y(), point.z());
    }

    private static StateType xyzToRGB(int x, int y, int z) {
        float hue = (float) (((Math.sin(x * 0.05) + Math.cos(z * 0.05)) * 0.5 + 0.5) % 1.0);
        float saturation = 1.0f;
        float brightness = 0.8f + 0.2f * Math.max(0.0f, Math.min(1.0f, (float) y / 255));

        java.awt.Color hsbColor = java.awt.Color.getHSBColor(hue, saturation, brightness);
        return getStateType(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue());
    }

    private static StateType getStateType(int red, int green, int blue) {
        int distance = Integer.MAX_VALUE;
        DyeColor closest = null;
        for (DyeColor dye : DyeColor.values()) {
            org.bukkit.Color color = dye.getColor();
            int dist = Math.abs(color.getRed() - red) + Math.abs(color.getGreen() - green) + Math.abs(color.getBlue() - blue);
            if (dist < distance) {
                distance = dist;
                closest = dye;
            }
        }

        StateType byName = StateTypes.getByName((closest.name() + "_STAINED_GLASS").toLowerCase(Locale.ROOT));
        if (byName == null) {
            return StateTypes.WHITE_STAINED_GLASS;
        }
        return byName;
    }


}
