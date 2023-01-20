package com.eternalcode.combat.listener;

import com.eternalcode.combat.NotificationAnnouncer;
import com.eternalcode.combat.combat.CombatManager;
import com.eternalcode.combat.config.implementation.MessageConfig;
import com.eternalcode.combat.config.implementation.PluginConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.UUID;

public class InventoryOpenListener implements Listener {

    private final CombatManager combatManager;
    private final NotificationAnnouncer announcer;
    private final MessageConfig messages;
    private final PluginConfig config;

    public InventoryOpenListener(CombatManager combatManager, NotificationAnnouncer announcer, MessageConfig messages, PluginConfig config) {
        this.combatManager = combatManager;
        this.announcer = announcer;
        this.messages = messages;
        this.config = config;
    }

    @EventHandler
    void onOpen(InventoryOpenEvent event) {
        if (!this.config.blockingInventories) {
            return;
        }

        UUID uniqueId = event.getPlayer().getUniqueId();

        if (!this.combatManager.isInCombat(uniqueId)) {
            return;
        }

        event.setCancelled(true);

        this.announcer.announceMessage(uniqueId, this.messages.inventoryBlocked);
    }
}
