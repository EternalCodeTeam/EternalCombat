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
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.papermc.lib.PaperLib;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChunkSnapshot;
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
    private final Cache<ChunkLocation, ChunkSnapshot> chunkCache = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.ofMillis(500))
        .build();

    public BorderBlockController(BorderService borderService, Scheduler scheduler, Server server) {
        this.borderService = borderService;
        this.server = server;
        scheduler.timerAsync(() -> this.updatePlayers(), Duration.ofMillis(200), Duration.ofMillis(200));
    }

    @EventHandler
    void onBorderShowAsyncEvent(BorderShowAsyncEvent event) {
        Player player = event.getPlayer();
        Set<BorderPoint> borderPoints = this.processChunks(player, event.getPoints());

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

            this.updatePlayer(uuid, player);
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

            this.playBlocks(player, border);
        }
    }

    record ChunkLocation(int x, int z) {}

    public void playBlocks(Player player, Collection<BorderPoint> blocks) {
        Map<Vector3i, Set<BorderPoint>> chunksToUpdate = this.splitToChunks(blocks);

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

    private Set<BorderPoint> processChunks(Player player, Collection<BorderPoint> blocks) {
        Map<ChunkLocation, Set<BorderPoint>> chunksToProcess = new HashMap<>();
        for (BorderPoint block : blocks) {
            BorderPoint inclusive = block.toInclusive();
            ChunkLocation location = new ChunkLocation(inclusive.x() >> 4, inclusive.z() >> 4);
            chunksToProcess.computeIfAbsent(location, k -> new HashSet<>()).add(inclusive);
        }

        Set<BorderPoint> points = new HashSet<>();
        chunksToProcess.forEach((location, chunkPoints) -> {
            ChunkSnapshot snapshot = loadChunkSnapshot(player, location);
            if (snapshot == null) {
                return;
            }

            for (BorderPoint point : chunkPoints) {
                Material type = snapshot.getBlockType(point.x() - (location.x() << 4), point.y(), point.z() - (location.z() << 4));
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
        StateType type = BorderColorUtil.xyzToColoredGlass(point.x(), point.y(), point.z());
        WrappedBlockState state = WrappedBlockState.getDefaultState(SERVER_VERSION, type);
        return new EncodedBlock(state.getGlobalId(), point.x(), point.y(), point.z());
    }

    private ChunkSnapshot loadChunkSnapshot(Player player, ChunkLocation location) {
        ChunkSnapshot snapshot = chunkCache.getIfPresent(location);
        if (snapshot != null) {
            return snapshot;
        }

        ChunkSnapshot chunkSnapshot = PaperLib.getChunkAtAsync(player.getWorld(), location.x(), location.z(), false)
            .thenApply(chunk -> chunk.getChunkSnapshot())
            .join();

        chunkCache.put(location, chunkSnapshot);
        return chunkSnapshot;
    }

}
