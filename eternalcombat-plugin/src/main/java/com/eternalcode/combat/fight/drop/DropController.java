package com.eternalcode.combat.fight.drop;

import com.eternalcode.combat.event.DynamicListener;
import com.eternalcode.combat.fight.FightManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.enchantments.Enchantment;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class DropController implements DynamicListener<PlayerDeathEvent> {

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

    @Override
    public void onEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();
        DropType dropType = this.dropSettings.dropType;
        boolean inCombat = this.fightManager.isInCombat(uuid);

        if (shouldHeadDrop(inCombat)) {
            addHeadDrop(event, player);
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
            event.setDroppedExp(result.droppedExp());
        }
    }

    private boolean shouldHeadDrop(boolean isCombat) {
        if (this.dropSettings.headDropOnlyInCombat && !isCombat) {
            return false;
        }

        if (!this.dropSettings.headDropEnabled || this.dropSettings.headDropChance <= 0.0) {
            return false;
        }

        return ThreadLocalRandom.current().nextDouble(0, 100) <= this.dropSettings.headDropChance;
    }

    private void addHeadDrop(PlayerDeathEvent event, Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);

        if (head.getItemMeta() instanceof SkullMeta meta) {
            String killerName = player.getKiller() != null ? player.getKiller().getName() : "Unknown";
            String displayName = this.dropSettings.headDropDisplayName
                .replace("{PLAYER}", player.getName())
                .replace("{KILLER}", killerName);

            meta.setOwningPlayer(player);
            meta.setDisplayName(displayName);

            if (!this.dropSettings.headDropLore.isEmpty()) {
                List<String> lore = this.dropSettings.headDropLore.stream()
                    .map(line -> line.replace("{PLAYER}", player.getName()).replace("{KILLER}", killerName))
                    .toList();
                meta.setLore(lore);
            }

            meta.addEnchant(Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            head.setItemMeta(meta);
        }

        event.getDrops().add(head);
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
