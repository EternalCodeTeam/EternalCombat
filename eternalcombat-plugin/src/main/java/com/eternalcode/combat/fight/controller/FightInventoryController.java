package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class FightInventoryController implements Listener {

    private final FightManager fightManager;
    private final PluginConfig config;
    private final NoticeService noticeService;

    public FightInventoryController(FightManager fightManager, PluginConfig config, NoticeService noticeService) {
        this.fightManager = fightManager;
        this.config = config;
        this.noticeService = noticeService;
    }

    @EventHandler
    void onOpenInventory(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        InventoryType inventoryType = event.getInventory().getType();


        boolean isInRestrictedList = this.config.inventory.restrictedInventoryTypes.contains(inventoryType);
        boolean shouldBlock = this.config.inventory.inventoryAccessMode.shouldBlock(isInRestrictedList);

        if (shouldBlock) {
            event.setCancelled(true);

            this.noticeService.create()
                .player(uniqueId)
                .notice(this.config.messagesSettings.inventoryBlockedDuringCombat)
                .send();
        }
    }
}
