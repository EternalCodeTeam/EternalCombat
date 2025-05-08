package com.eternalcode.combat.fight.drop;

import com.eternalcode.combat.fight.FightManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class DropController implements Listener {

    private final DropService dropService;
    private final DropKeepInventoryService keepInventoryManager;
    private final DropSettings dropSettings;
    private final FightManager fightManager;

    public DropController(DropService dropService, DropKeepInventoryService keepInventoryManager, DropSettings dropSettings, FightManager fightManager) {
        this.dropService = dropService;
        this.keepInventoryManager = keepInventoryManager;
        this.dropSettings = dropSettings;
        this.fightManager = fightManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeathLowest(PlayerDeathEvent event) {
        if (this.dropSettings.dropEventPriority == EventPriority.LOWEST) {
            this.handleDeath(event);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDeathLow(PlayerDeathEvent event) {
        if (this.dropSettings.dropEventPriority == EventPriority.LOW) {
            this.handleDeath(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDeathNormal(PlayerDeathEvent event) {
        if (this.dropSettings.dropEventPriority == EventPriority.NORMAL) {
            this.handleDeath(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeathHigh(PlayerDeathEvent event) {
        if (this.dropSettings.dropEventPriority == EventPriority.HIGH) {
            this.handleDeath(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeathHighest(PlayerDeathEvent event) {
        if (this.dropSettings.dropEventPriority == EventPriority.HIGHEST) {
            this.handleDeath(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathMonitor(PlayerDeathEvent event) {
        if (this.dropSettings.dropEventPriority == EventPriority.MONITOR) {
            this.handleDeath(event);
        }
    }

    private void handleDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();
        DropType dropType = this.dropSettings.dropType;
        boolean inCombat = this.fightManager.isInCombat(uuid);

        if (this.dropSettings.headDropEnabled && this.dropSettings.headDropChance > 0.0) {
            boolean shouldDrop = (!this.dropSettings.headDropOnlyInCombat || inCombat)
                && ThreadLocalRandom.current().nextDouble(0, 100) <= this.dropSettings.headDropChance;

            if (shouldDrop) {
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();

                if (meta != null) {
                    meta.setOwningPlayer(player);
                    String killerName = player.getKiller() != null ? player.getKiller().getName() : "Unknown";

                    String displayName = this.dropSettings.headDropDisplayName
                        .replace("{PLAYER}", player.getName())
                        .replace("{KILLER}", killerName);
                    meta.setDisplayName(displayName);

                    if (!this.dropSettings.headDropLore.isEmpty()) {
                        List<String> lore = this.dropSettings.headDropLore.stream()
                            .map(line -> line.replace("{PLAYER}", player.getName()).replace("{KILLER}", killerName))
                            .toList();
                        meta.setLore(lore);
                    }

                    head.setItemMeta(meta);
                }

                event.getDrops().add(head);
            }
        }

        if (dropType == DropType.UNCHANGED || !inCombat) {
            return;
        }

        List<ItemStack> drops = event.getDrops();

        Drop drop = Drop.builder()
            .player(player)
            .killer(player.getKiller())
            .droppedItems(drops)
            .droppedExp(player.getTotalExperience())
            .build();

        DropResult result = this.dropService.modify(dropType, drop);

        if (result == null) {
            return;
        }

        drops.clear();
        drops.addAll(result.droppedItems());

        this.keepInventoryManager.addItems(uuid, result.removedItems());

        if (this.dropSettings.affectExperience) {
            event.setDroppedExp(drop.getDroppedExp());
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerUniqueId = player.getUniqueId();

        if (this.keepInventoryManager.hasItems(playerUniqueId)) {
            PlayerInventory playerInventory = player.getInventory();

            ItemStack[] itemsToGive = this.keepInventoryManager.nextItems(playerUniqueId)
                .toArray(new ItemStack[0]);

            playerInventory.addItem(itemsToGive);
        }
    }
}
