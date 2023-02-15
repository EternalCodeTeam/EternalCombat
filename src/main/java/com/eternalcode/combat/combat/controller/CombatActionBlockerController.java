package com.eternalcode.combat.combat.controller;

import com.eternalcode.combat.combat.CombatManager;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.UUID;

public class CombatActionBlockerController implements Listener {

    private final CombatManager combatManager;
    private final NotificationAnnouncer announcer;
    private final PluginConfig config;

    public CombatActionBlockerController(CombatManager combatManager, NotificationAnnouncer announcer, PluginConfig config) {
        this.combatManager = combatManager;
        this.announcer = announcer;
        this.config = config;
    }

    @EventHandler
    void onPlace(BlockPlaceEvent event) {
        if (!this.config.settings.blockPlace) {
            return;
        }

        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.combatManager.isInCombat(uniqueId)) {
            return;
        }

        Block block = event.getBlock();
        int level = block.getY();

        if (level > this.config.settings.blockPlaceLevel) {
            return;
        }

        event.setCancelled(true);
        this.announcer.sendMessage(player, this.config.messages.blockPlaceBlocked);
    }

    @EventHandler
    void onOpenInventory(InventoryOpenEvent event) {
        if (!this.config.settings.blockingInventories) {
            return;
        }

        Player player = (Player) event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.combatManager.isInCombat(uniqueId)) {
            return;
        }

        event.setCancelled(true);

        this.announcer.sendMessage(player, this.config.messages.inventoryBlocked);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID playerUniqueId = player.getUniqueId();

        if (!this.combatManager.isInCombat(playerUniqueId)) {
            return;
        }

        String command = event.getMessage();

        for (String blockedCommand : this.config.settings.blockedCommands) {
            if (command.startsWith(blockedCommand)) {

                event.setCancelled(true);
                this.announcer.sendMessage(player, config.messages.cantUseCommand);
                break;
            }
        }
    }
}
