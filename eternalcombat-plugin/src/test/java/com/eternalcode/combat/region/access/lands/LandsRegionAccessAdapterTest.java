package com.eternalcode.combat.region.access.lands;

import com.eternalcode.combat.region.Region;
import com.eternalcode.combat.region.Point;
import com.eternalcode.combat.region.access.RegionAccessConfig;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.nation.Nation;
import me.angeschossen.lands.api.player.LandPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LandsRegionAccessAdapterTest {

    @Mock
    private Plugin plugin;
    
    @Mock
    private RegionAccessConfig config;
    
    @Mock
    private Logger logger;
    
    @Mock
    private LandsIntegration lands;
    
    @Mock
    private Player player;
    
    @Mock
    private Region region;
    
    @Mock
    private Point point;
    
    @Mock
    private World world;
    
    @Mock
    private Location location;
    
    @Mock
    private LandPlayer landPlayer;
    
    @Mock
    private Land targetLand;
    
    @Mock
    private Land playerLand;
    
    @Mock
    private Nation playerNation;
    
    @Mock
    private Nation targetNation;

    private LandsRegionAccessAdapter adapter;

    @BeforeEach
    void setUp() {
        when(config.wartimeEnabled).thenReturn(true);
        when(config.bypassPermission).thenReturn("eternalcombat.bypass.region");
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(region.getCenter()).thenReturn(point);
        when(point.toLocation()).thenReturn(location);
        when(location.getWorld()).thenReturn(world);
        when(location.getBlockX()).thenReturn(100);
        when(location.getBlockZ()).thenReturn(200);
        
        adapter = new LandsRegionAccessAdapter(lands, config, logger);
    }

    @Test
    void canEnter_WhenWartimeDisabled_ShouldReturnFalse() {
        when(config.wartimeEnabled).thenReturn(false);

        boolean result = adapter.canEnter(player, region);

        assertFalse(result);
    }

    @Test
    void canEnter_WhenPlayerHasBypassPermission_ShouldReturnTrue() {
        when(player.hasPermission("eternalcombat.bypass.region")).thenReturn(true);

        boolean result = adapter.canEnter(player, region);

        assertTrue(result);
    }

    @Test
    void canEnter_WhenPlayerNotInAnyLand_ShouldReturnFalse() {
        when(lands.getLandPlayer(any(UUID.class))).thenReturn(null);

        boolean result = adapter.canEnter(player, region);

        assertFalse(result);
    }

    @Test
    void canEnter_WhenNoTargetLand_ShouldReturnTrue() {
        when(lands.getLandPlayer(any(UUID.class))).thenReturn(landPlayer);
        when(lands.getLandByChunk(any(World.class), anyInt(), anyInt())).thenReturn(null);

        boolean result = adapter.canEnter(player, region);

        assertTrue(result);
    }

    @Test
    void canEnter_WhenPlayerInOwnLand_ShouldReturnTrue() {
        when(lands.getLandPlayer(any(UUID.class))).thenReturn(landPlayer);
        when(lands.getLandByChunk(any(World.class), anyInt(), anyInt())).thenReturn(targetLand);
        when(landPlayer.getLands()).thenReturn(Set.of(targetLand));

        boolean result = adapter.canEnter(player, region);

        assertTrue(result);
    }

    @Test
    void canEnter_WhenNationsAtWar_ShouldReturnTrue() {
        when(lands.getLandPlayer(any(UUID.class))).thenReturn(landPlayer);
        when(lands.getLandByChunk(any(World.class), anyInt(), anyInt())).thenReturn(targetLand);
        when(landPlayer.getLands()).thenReturn(Set.of(playerLand));
        when(playerLand.getNation()).thenReturn(playerNation);
        when(targetLand.getNation()).thenReturn(targetNation);
        when(playerNation.isEnemy(targetNation)).thenReturn(true);

        boolean result = adapter.canEnter(player, region);

        assertTrue(result);
    }

    @Test
    void canEnter_WhenNationsNotAtWar_ShouldReturnFalse() {
        when(lands.getLandPlayer(any(UUID.class))).thenReturn(landPlayer);
        when(lands.getLandByChunk(any(World.class), anyInt(), anyInt())).thenReturn(targetLand);
        when(landPlayer.getLands()).thenReturn(Set.of(playerLand));
        when(playerLand.getNation()).thenReturn(playerNation);
        when(targetLand.getNation()).thenReturn(targetNation);
        when(playerNation.isEnemy(targetNation)).thenReturn(false);
        when(targetNation.isEnemy(playerNation)).thenReturn(false);

        boolean result = adapter.canEnter(player, region);

        assertFalse(result);
    }

}
