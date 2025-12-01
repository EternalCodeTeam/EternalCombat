package com.eternalcode.combat.border.animation.block;

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
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BorderBlockController implements Listener {

    public static final PacketEventsAPI<?> PACKET_EVENTS = PacketEvents.getAPI();
    public static final PlayerManager PLAYER_MANAGER = PACKET_EVENTS.getPlayerManager();
    public static final ClientVersion SERVER_VERSION = PACKET_EVENTS.getServerManager().getVersion().toClientVersion();

    public static final int AIR_ID = WrappedBlockState.getDefaultState(SERVER_VERSION, StateTypes.AIR).getGlobalId();
    public static final int MINECRAFT_CHUNK_SHIFT = 4;

    private final BorderService borderService;
    private final Supplier<BlockSettings> settings;
    private final Server server;

    private final Set<UUID> playersToUpdate = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Object> lockedPlayers = new ConcurrentHashMap<>();
    private final Map<UUID, Map<BorderPoint, Integer>> originalBlocks = new ConcurrentHashMap<>();
    private final ChunkCache chunkCache;

    public BorderBlockController(BorderService borderService, Supplier<BlockSettings> settings, Scheduler scheduler, Server server) {
        this.borderService = borderService;
        this.settings = settings;
        this.server = server;
        this.chunkCache = new ChunkCache(settings.get());

        scheduler.timerAsync(this::updatePlayers, settings.get().updateDelay, settings.get().updateDelay);
    }

    @EventHandler
    void onBorderShowAsyncEvent(BorderShowAsyncEvent event) {
        if (!this.settings.get().enabled) {
            return;
        }

        Player player = event.getPlayer();
        Set<BorderPoint> borderPoints = this.filterPassablePoints(player, event.getPoints());

        event.setPoints(borderPoints);
        this.showBlocks(player, borderPoints);
        this.playersToUpdate.add(player.getUniqueId());
    }

    @EventHandler
    void onBorderHideAsyncEvent(BorderHideAsyncEvent event) {
        if (!this.settings.get().enabled) {
            return;
        }

        UUID playerId = event.getPlayer().getUniqueId();
        Object lock = this.lockedPlayers.computeIfAbsent(playerId, k -> new Object());

        synchronized (lock) {
            this.restoreBlocks(event.getPlayer(), event.getPoints());

            Set<BorderPoint> border = this.borderService.getActiveBorder(event.getPlayer());
            if (border.isEmpty()) {
                this.playersToUpdate.remove(playerId);
                this.originalBlocks.remove(playerId);
            }
        }
    }

    private void updatePlayers() {
        if (!this.settings.get().enabled) {
            return;
        }

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
        Object lock = this.lockedPlayers.computeIfAbsent(uuid, k -> new Object());
        synchronized (lock) {
            Set<BorderPoint> border = this.borderService.getActiveBorder(player);

            if (border.isEmpty()) {
                this.playersToUpdate.remove(uuid);
                return;
            }

            this.showBlocks(player, border);
        }
    }

    private void showBlocks(Player player, Collection<BorderPoint> blocks) {
        this.splitByChunkSection(blocks).forEach((chunkPos, chunkPoints) -> {
            EncodedBlock[] encodedBlocks = chunkPoints.stream()
                .map(this::toBorderBlock)
                .toArray(EncodedBlock[]::new);

            PLAYER_MANAGER.sendPacket(player, new WrapperPlayServerMultiBlockChange(chunkPos, true, encodedBlocks));
        });
    }

    private void restoreBlocks(Player player, Collection<BorderPoint> blocks) {
        UUID playerId = player.getUniqueId();
        Map<BorderPoint, Integer> savedBlocks = this.originalBlocks.get(playerId);

        this.splitByChunkSection(blocks).forEach((chunkPos, chunkPoints) -> {
            EncodedBlock[] encodedBlocks = chunkPoints.stream()
                .map(point -> this.toOriginalBlock(point, savedBlocks))
                .toArray(EncodedBlock[]::new);

            PLAYER_MANAGER.sendPacket(player, new WrapperPlayServerMultiBlockChange(chunkPos, true, encodedBlocks));
        });

        if (savedBlocks != null) {
            blocks.forEach(savedBlocks::remove);
        }
    }

    private Set<BorderPoint> filterPassablePoints(Player player, Collection<BorderPoint> points) {
        UUID playerId = player.getUniqueId();
        Map<BorderPoint, Integer> savedBlocks = this.originalBlocks.computeIfAbsent(playerId, key -> new ConcurrentHashMap<>());

        return this.groupByChunk(points).entrySet().stream()
            .flatMap(entry -> this.filterChunkPoints(player, entry, savedBlocks))
            .collect(Collectors.toSet());
    }

    private Stream<BorderPoint> filterChunkPoints(Player player, Entry<ChunkLocation, Set<BorderPoint>> entry, Map<BorderPoint, Integer> savedBlocks) {
        ChunkSnapshot snapshot = this.chunkCache.loadSnapshot(player, entry.getKey());

        if (snapshot == null) {
            return Stream.empty();
        }

        ChunkLocation chunk = entry.getKey();

        return entry.getValue().stream().filter(point -> this.isPassableAndSave(point, chunk, snapshot, savedBlocks));
    }

    private boolean isPassableAndSave(BorderPoint point, ChunkLocation chunk, ChunkSnapshot snapshot, Map<BorderPoint, Integer> savedBlocks) {
        if (savedBlocks.containsKey(point)) {
            return true;
        }

        int localX = point.x() - (chunk.x() << MINECRAFT_CHUNK_SHIFT);
        int localZ = point.z() - (chunk.z() << MINECRAFT_CHUNK_SHIFT);

        Material material = snapshot.getBlockType(localX, point.y(), localZ);

        if (material.isSolid()) {
            return false;
        }

        savedBlocks.put(point, this.resolveBlockId(material));

        return true;
    }

    private int resolveBlockId(Material material) {
        StateType stateType = StateTypes.getByName("minecraft:" + material.name().toLowerCase(Locale.ROOT));

        if (stateType == null) {
            return AIR_ID;
        }

        return WrappedBlockState.getDefaultState(SERVER_VERSION, stateType).getGlobalId();
    }

    private EncodedBlock toBorderBlock(BorderPoint point) {
        StateType type = this.settings.get().type.getStateType(point);
        int blockId = WrappedBlockState.getDefaultState(SERVER_VERSION, type).getGlobalId();

        return new EncodedBlock(blockId, point.x(), point.y(), point.z());
    }

    private EncodedBlock toOriginalBlock(BorderPoint point, Map<BorderPoint, Integer> savedBlocks) {
        int blockId = savedBlocks != null ? savedBlocks.getOrDefault(point, AIR_ID) : AIR_ID;

        return new EncodedBlock(blockId, point.x(), point.y(), point.z());
    }

    private Map<ChunkLocation, Set<BorderPoint>> groupByChunk(Collection<BorderPoint> points) {
        return points.stream().collect(Collectors.groupingBy(
            point -> new ChunkLocation(point.x() >> MINECRAFT_CHUNK_SHIFT, point.z() >> MINECRAFT_CHUNK_SHIFT),
            Collectors.toSet()
        ));
    }

    private Map<Vector3i, Set<BorderPoint>> splitByChunkSection(Collection<BorderPoint> points) {
        return points.stream().collect(Collectors.groupingBy(
            point -> new Vector3i(point.x() >> MINECRAFT_CHUNK_SHIFT, point.y() >> MINECRAFT_CHUNK_SHIFT, point.z() >> MINECRAFT_CHUNK_SHIFT),
            Collectors.toSet()
        ));
    }

}
