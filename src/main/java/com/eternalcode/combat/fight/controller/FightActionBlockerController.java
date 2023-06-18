package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightCommandMode;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.FightTag;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.util.DurationUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import panda.utilities.text.Formatter;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class FightActionBlockerController implements Listener {

    private final FightManager fightManager;
    private final NotificationAnnouncer announcer;
    private final PluginConfig config;

    public FightActionBlockerController(FightManager fightManager, NotificationAnnouncer announcer, PluginConfig config) {
        this.fightManager = fightManager;
        this.announcer = announcer;
        this.config = config;
    }

    @EventHandler
    void onPlace(BlockPlaceEvent event) {
        if (!this.config.settings.shouldPreventBlockPlacing) {
            return;
        }

        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        Block block = event.getBlock();
        int level = block.getY();

        List<Material> specificBlocksToPreventPlacing = this.config.settings.specificBlocksToPreventPlacing;

        boolean isBlockPlaceLevel = level > this.config.settings.minBlockPlacingLevel;
        if (isBlockPlaceLevel && specificBlocksToPreventPlacing.isEmpty()) {
            event.setCancelled(true);
            this.announcer.sendMessage(player, this.config.messages.blockPlacingBlockedDuringCombat);
        }

        Material blockMaterial = block.getType();
        boolean isBlockInDisabledList = specificBlocksToPreventPlacing.contains(blockMaterial);
        if (isBlockPlaceLevel && isBlockInDisabledList) {
            event.setCancelled(true);
            this.announcer.sendMessage(player, this.config.messages.blockPlacingBlockedDuringCombat);
        }
    }

    @EventHandler
    public void onToggleGlide(EntityToggleGlideEvent event) {
        if (!this.config.settings.shouldPreventElytraUsage) {
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
    public void onDamage(EntityDamageEvent event) {
        if (!this.config.settings.shouldElytraDisableOnDamage) {
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
    void onOpenInventory(InventoryOpenEvent event) {
        if (!this.config.settings.shouldPreventInventoryOpening) {
            return;
        }

        Player player = (Player) event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        event.setCancelled(true);

        this.announcer.sendMessage(player, this.config.messages.inventoryBlockedDuringCombat);
    }

    @EventHandler
    void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.ENDER_PEARL) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (this.config.settings.shouldBlockThrowingPearls) {
            event.setCancelled(true);

            this.announcer.sendMessage(player, this.config.messages.pearlThrowBlockedDuringCombat);
        }
        else if (this.config.settings.shouldBlockThrowingPearlsWithDelay) {
            FightTag fightTag = this.fightManager.getFightTag(uniqueId);

            if (!fightTag.isPearlDelayExpired()) {
                event.setCancelled(true);

                Duration remainingPearlDelay = fightTag.getRemainingPearlDelay();

                Formatter formatter = new Formatter()
                    .register("{TIME}", DurationUtil.format(remainingPearlDelay));

                String format = formatter.format(this.config.messages.pearlThrowBlockedDelayDuringCombat);
                this.announcer.sendMessage(player, format);
                return;
            }

            Duration pearlThrowDuration = this.config.settings.pearlThrowDuration;

            Instant now = Instant.now();
            Instant endOfPearlDelay = now.plus(pearlThrowDuration);

            fightTag.setEndOfPearlDelay(endOfPearlDelay);
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID playerUniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(playerUniqueId)) {
            return;
        }

        String command = event.getMessage().split(" ")[0].substring(1).toLowerCase();

        boolean isMatchCommand = this.config.settings.blockedCommands.stream()
            .anyMatch(command::startsWith);

        FightCommandMode mode = this.config.settings.commandBlockingMode;

        boolean shouldCancel = mode.shouldBlock(isMatchCommand);

        if (shouldCancel) {
            event.setCancelled(true);
            this.announcer.sendMessage(player, this.config.messages.commandDisabledDuringCombat);
        }
    }
}
