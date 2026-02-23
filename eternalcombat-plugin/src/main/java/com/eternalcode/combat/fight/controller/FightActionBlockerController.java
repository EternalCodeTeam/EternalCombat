package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.config.implementation.BlockPlacementSettings;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import com.eternalcode.combat.notification.NoticeService;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.List;
import java.util.UUID;

public class FightActionBlockerController implements Listener {

    private final FightManager fightManager;
    private final NoticeService noticeService;
    private final PluginConfig config;
    private final Server server;

    public FightActionBlockerController(FightManager fightManager, NoticeService noticeService, PluginConfig config, Server server) {
        this.fightManager = fightManager;
        this.noticeService = noticeService;
        this.config = config;
        this.server = server;
    }

    @EventHandler
    void onPlace(BlockPlaceEvent event) {
        if (!this.config.blockPlacement.disableBlockPlacing) {
            return;
        }

        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        Block block = event.getBlock();
        int level = block.getY();

        List<Material> specificBlocksToPreventPlacing = this.config.blockPlacement.restrictedBlockTypes;

        boolean isPlacementBlocked = this.isPlacementBlocked(level);

        if (isPlacementBlocked && specificBlocksToPreventPlacing.isEmpty()) {
            event.setCancelled(true);
            this.noticeService.create()
                .player(uniqueId)
                .notice(this.config.messagesSettings.blockPlacingBlockedDuringCombat)
                .placeholder("{Y}", String.valueOf(this.config.blockPlacement.blockPlacementYCoordinate))
                .placeholder("{MODE}", this.config.blockPlacement.blockPlacementModeDisplayName)
                .send();

        }

        Material blockMaterial = block.getType();
        boolean isBlockInDisabledList = specificBlocksToPreventPlacing.contains(blockMaterial);
        if (isPlacementBlocked && isBlockInDisabledList) {
            event.setCancelled(true);

            this.noticeService.create()
                .player(uniqueId)
                .notice(this.config.messagesSettings.blockPlacingBlockedDuringCombat)
                .placeholder("{Y}", String.valueOf(this.config.blockPlacement.blockPlacementYCoordinate))
                .placeholder("{MODE}", this.config.blockPlacement.blockPlacementModeDisplayName)
                .send();

        }
    }

    private boolean isPlacementBlocked(int level) {
        return this.config.blockPlacement.blockPlacementMode == BlockPlacementSettings.BlockPlacingMode.ABOVE
            ? level > this.config.blockPlacement.blockPlacementYCoordinate
            : level < this.config.blockPlacement.blockPlacementYCoordinate;
    }

    @EventHandler
    void onToggleGlide(EntityToggleGlideEvent event) {
        if (!this.config.combat.disableElytraUsage) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        if (event.isGliding()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onMoveWhileGliding(PlayerMoveEvent event) {
        if (!this.config.combat.disableElytraUsage) {
            return;
        }

        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        if (player.isGliding()) {
            player.setGliding(false);
        }
    }



    @EventHandler
    void onFly(PlayerToggleFlightEvent event) {
        if (!this.config.combat.disableFlying) {
            return;
        }

        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        if (event.isFlying()) {
            player.setAllowFlight(false);

            event.setCancelled(true);
        }
    }

    @EventHandler
    void onUnTag(FightUntagEvent event) {
        if (!this.config.combat.disableFlying) {
            return;
        }

        UUID uniqueId = event.getPlayer();
        Player player = this.server.getPlayer(uniqueId);

        if (player == null) {
            return;
        }
        GameMode playerGameMode = player.getGameMode();
        if (playerGameMode == GameMode.CREATIVE || playerGameMode == GameMode.SPECTATOR) {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    void onDamage(EntityDamageEvent event) {
        if (!this.config.combat.disableElytraOnDamage) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        UUID uniqueId = player.getUniqueId();

        if (this.fightManager.isInCombat(uniqueId) && player.isGliding()) {
            player.setGliding(false);
        }
    }

}
