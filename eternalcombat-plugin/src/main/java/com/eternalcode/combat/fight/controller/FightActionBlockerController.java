package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.WhitelistBlacklistMode;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

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
            player.setGliding(false);
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

            player.setFallDistance(0f);
            player.setVelocity(player.getVelocity().setY(-1));
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
    void onTag(com.eternalcode.combat.fight.event.FightTagEvent event) {
        UUID uniqueId = event.getPlayer();
        Player player = this.server.getPlayer(uniqueId);

        if (player == null) {
            return;
        }

        if (this.config.combat.disableFlying) {
            GameMode gameMode = player.getGameMode();

            if (gameMode != GameMode.CREATIVE && gameMode != GameMode.SPECTATOR) {
                player.setAllowFlight(false);
                player.setFlying(false);
            }
        }

        if (this.config.combat.unequipElytraOnCombat) {
            ItemStack chest = player.getInventory().getChestplate();

            if (chest != null && chest.getType() == Material.ELYTRA) {
                removeChestplateIfElytra(player);

                this.noticeService.create()
                    .player(uniqueId)
                    .notice(this.config.messagesSettings.elytraDisabledDuringCombat)
                    .send();
            }
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

    @EventHandler
    void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID playerUniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(playerUniqueId)) {
            return;
        }

        String command = event.getMessage().substring(1);

        boolean isAnyMatch = this.config.commands.restrictedCommands.stream()
            .anyMatch(restrictedCommand -> StringUtil.startsWithIgnoreCase(command, restrictedCommand));

        WhitelistBlacklistMode mode = this.config.commands.commandRestrictionMode;

        boolean shouldCancel = mode.shouldBlock(isAnyMatch);

        if (shouldCancel) {
            event.setCancelled(true);
            this.noticeService.create()
                .player(playerUniqueId)
                .notice(this.config.messagesSettings.commandDisabledDuringCombat)
                .send();

        }
    }

    @EventHandler
    void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!this.config.combat.unequipElytraOnCombat) {
            return;
        }

        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        if (event.getCurrentItem() == null) {
            return;
        }

        if (event.getCurrentItem().getType() == Material.ELYTRA) {
            event.setCancelled(true);

            this.noticeService.create()
                .player(uniqueId)
                .notice(this.config.messagesSettings.elytraDisabledDuringCombat)
                .send();
        }
    }

    private void removeChestplateIfElytra(Player player) {
        ItemStack chestplate = player.getInventory().getChestplate();

        if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
            player.getInventory().setChestplate(null);

            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(chestplate);
            if (!leftover.isEmpty()) {
                leftover.values().forEach(item ->
                    player.getWorld().dropItemNaturally(player.getLocation(), item)
                );
            }
        }
    }
}
