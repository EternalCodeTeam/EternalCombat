package com.eternalcode.combat.border.animation.block;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.papermc.lib.PaperLib;
import org.bukkit.ChunkSnapshot;
import org.bukkit.entity.Player;

class ChunkCache {

    private final Cache<ChunkLocation, ChunkSnapshot> chunkCache;

    ChunkCache(BlockSettings settings) {
        this.chunkCache = CacheBuilder.newBuilder()
            .expireAfterWrite(settings.chunkCacheDelay)
            .build();
    }

    public ChunkSnapshot loadSnapshot(Player player, ChunkLocation location) {
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
