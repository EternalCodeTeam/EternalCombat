package com.eternalcode.combat.fight.blocker;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.config.implementation.BlockPlacementSettings;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;
import java.util.UUID;

public class PlaceBlockBlocker implements Listener {

    private final FightManager fightManager;
    private final NoticeService noticeService;
    private final PluginConfig config;

    public PlaceBlockBlocker(FightManager fightManager, NoticeService noticeService, PluginConfig config) {
        this.fightManager = fightManager;
        this.noticeService = noticeService;
        this.config = config;
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

}
