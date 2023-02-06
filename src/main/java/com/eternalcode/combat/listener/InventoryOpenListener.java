package com.eternalcode.combat.listener;

import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.combat.CombatManager;
import com.eternalcode.combat.config.implementation.PluginConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.UUID;

public class InventoryOpenListener implements Listener {

    private final CombatManager combatManager;
    private final NotificationAnnouncer announcer;
    private final PluginConfig config;

    public InventoryOpenListener(CombatManager combatManager, NotificationAnnouncer announcer, PluginConfig config) {
        this.combatManager = combatManager;
        this.announcer = announcer;
        this.config = config;
    }

    @EventHandler
    void onOpen(InventoryOpenEvent event) {
        if (!this.config.settings.blockingInventories) {
            return;
        }

        UUID uniqueId = event.getPlayer().getUniqueId();
        Player player = (Player) event.getPlayer();

        if (!this.combatManager.isInCombat(uniqueId)) {
            return;
        }

        event.setCancelled(true);

        this.announcer.sendMessage(player, this.config.messages.inventoryBlocked);
    }
}
