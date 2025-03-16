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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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
    private final BlockSettings settings;
    private final Server server;

    private final Set<UUID> playersToUpdate = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Object> lockedPlayers = new ConcurrentHashMap<>();
    private final ChunkCache chunkCache;

    public BorderBlockController(BorderService borderService, BlockSettings settings, Scheduler scheduler, Server server) {
        this.borderService = borderService;
        this.settings = settings;
        this.server = server;
        this.chunkCache = new ChunkCache(settings);

        scheduler.timerAsync(() -> this.updatePlayers(), settings.updateDelay, settings.updateDelay);
    }

    @EventHandler
    void onBorderShowAsyncEvent(BorderShowAsyncEvent event) {
        if (!settings.enabled) {
            return;
        }

        Player player = event.getPlayer();
        Set<BorderPoint> borderPoints = this.getPointsWithoutAir(player, event.getPoints());

        event.setPoints(borderPoints);
        this.showBlocks(player, borderPoints);
        this.playersToUpdate.add(player.getUniqueId());
    }

    @EventHandler
    void onBorderHideAsyncEvent(BorderHideAsyncEvent event) {
        if (!settings.enabled) {
            return;
        }

        Object lock = lockedPlayers.computeIfAbsent(event.getPlayer().getUniqueId(), k -> new Object());
        synchronized (lock) {
            this.hideBlocks(event.getPlayer(), event.getPoints());

            Set<BorderPoint> border = this.borderService.getActiveBorder(event.getPlayer());
            if (border.isEmpty()) {
                this.playersToUpdate.remove(event.getPlayer().getUniqueId());
            }
        }
    }

    private void updatePlayers() {
        if (!settings.enabled) {
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
        Object lock = lockedPlayers.computeIfAbsent(uuid, k -> new Object());
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
        this.splitByChunks(blocks).entrySet().stream()
            .map(chunkBlocks -> toMultiBlockChangePacket(chunkBlocks))
            .forEach(change -> PLAYER_MANAGER.sendPacket(player, change));
    }

    private WrapperPlayServerMultiBlockChange toMultiBlockChangePacket(Entry<Vector3i, Set<BorderPoint>> chunkBlocks) {
        EncodedBlock[] encodedBlocks = chunkBlocks.getValue().stream()
            .map(borderPoint -> this.toEncodedBlock(borderPoint))
            .toArray(value -> new EncodedBlock[value]);

        return new WrapperPlayServerMultiBlockChange(chunkBlocks.getKey(), true, encodedBlocks);
    }

    private void hideBlocks(Player player, Collection<BorderPoint> blocks) {
        this.splitByChunks(blocks).entrySet().stream()
            .map(chunkBlocks -> toMultiAirChangePacket(chunkBlocks))
            .forEach(change -> PLAYER_MANAGER.sendPacket(player, change));
    }

    private WrapperPlayServerMultiBlockChange toMultiAirChangePacket(Entry<Vector3i, Set<BorderPoint>> chunkBlocks) {
        EncodedBlock[] encodedBlocks = chunkBlocks.getValue().stream()
            .map(point -> new EncodedBlock(AIR_ID, point.x(), point.y(), point.z()))
            .toArray(value -> new EncodedBlock[value]);

        return new WrapperPlayServerMultiBlockChange(chunkBlocks.getKey(), true, encodedBlocks);
    }

    private Set<BorderPoint> getPointsWithoutAir(Player player, Collection<BorderPoint> blocks) {
        Map<ChunkLocation, Set<BorderPoint>> chunksToProcess = blocks.stream()
            .map(borderPoint -> borderPoint.toInclusive())
            .collect(Collectors.groupingBy(
                inclusive -> new ChunkLocation(inclusive.x() >> MINECRAFT_CHUNK_SHIFT, inclusive.z() >> MINECRAFT_CHUNK_SHIFT),
                Collectors.toSet()
            ));

        return chunksToProcess.entrySet().stream()
            .flatMap(entry -> getPointsWithoutAirOnChunk(player, entry))
            .collect(Collectors.toSet());
    }

    private Stream<BorderPoint> getPointsWithoutAirOnChunk(Player player, Entry<ChunkLocation, Set<BorderPoint>> entry) {
        ChunkSnapshot snapshot =  this.chunkCache.loadSnapshot(player, entry.getKey());
        if (snapshot == null) {
            return Stream.empty();
        }

        return entry.getValue().stream()
            .filter(point -> isAir(entry.getKey(), point, snapshot));
    }

    private static boolean isAir(ChunkLocation location, BorderPoint point, ChunkSnapshot chunk) {
        int xInsideChunk = point.x() - (location.x() << MINECRAFT_CHUNK_SHIFT);
        int zInsideChunk = point.z() - (location.z() << MINECRAFT_CHUNK_SHIFT);
        Material material = chunk.getBlockType(xInsideChunk, point.y(), zInsideChunk);

        return material.isAir();
    }

    private Map<Vector3i, Set<BorderPoint>> splitByChunks(Collection<BorderPoint> blocks) {
        return blocks.stream().collect(Collectors.groupingBy(
            block -> new Vector3i(
                block.x() >> MINECRAFT_CHUNK_SHIFT,
                block.y() >> MINECRAFT_CHUNK_SHIFT,
                block.z() >> MINECRAFT_CHUNK_SHIFT
            ),
            Collectors.toSet()
        ));
    }

    private EncodedBlock toEncodedBlock(BorderPoint point) {
        StateType type = settings.type.getStateType(point);
        WrappedBlockState state = WrappedBlockState.getDefaultState(SERVER_VERSION, type);
        return new EncodedBlock(state.getGlobalId(), point.x(), point.y(), point.z());
    }

}
