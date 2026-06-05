package com.eternalcode.combat.fight.blocker;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.FightTagEvent;
import com.eternalcode.combat.notification.NoticeService;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ElytraEquipBlocker implements Listener {

    private final FightManager fightManager;
    private final NoticeService noticeService;
    private final PluginConfig config;
    private final Server server;

    public ElytraEquipBlocker(FightManager fightManager, NoticeService noticeService, PluginConfig config, Server server) {
        this.fightManager = fightManager;
        this.noticeService = noticeService;
        this.config = config;
        this.server = server;
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


    @EventHandler
    void onTag(FightTagEvent event) {
        UUID uniqueId = event.getPlayer();
        Player player = this.server.getPlayer(uniqueId);

        if (player == null) {
            return;
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

    private void removeChestplateIfElytra(Player player) {
        ItemStack chestplate = player.getInventory().getChestplate();

        if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
            player.getInventory().setChestplate(null);

            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(chestplate);
            leftover.values().forEach(item ->
                player.getWorld().dropItemNaturally(player.getLocation(), item)
            );
        }
    }

    @EventHandler
    void onInteract(PlayerInteractEvent event) {
        if (!this.config.combat.unequipElytraOnCombat) {
            return;
        }

        if (event.getItem() == null) {
            return;
        }

        if (event.getItem().getType() != Material.ELYTRA) {
            return;
        }

        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        switch (event.getAction()) {
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                event.setCancelled(true);

                this.noticeService.create()
                    .player(uniqueId)
                    .notice(this.config.messagesSettings.elytraDisabledDuringCombat)
                    .send();
            }
        }
    }

}
