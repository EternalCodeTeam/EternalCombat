package com.eternalcode.combat.drop;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DropKeepInventoryManager {

    private final Map<UUID, List<ItemStack>> itemsToGiveAfterRespawn = new HashMap<>();

    public void addItem(UUID uuid, ItemStack item) {
        this.itemsToGiveAfterRespawn.computeIfAbsent(uuid, k -> new ArrayList<>()).add(item);
    }

    public void addItems(UUID uuid, List<ItemStack> item) {
        item.forEach(i -> this.addItem(uuid, i));
    }

    public boolean hasItems(UUID uuid) {
        return this.itemsToGiveAfterRespawn.containsKey(uuid);
    }

    public List<ItemStack> nextItems(UUID uuid) {
        List<ItemStack> itemStacks = this.itemsToGiveAfterRespawn.remove(uuid);

        if (itemStacks == null) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(itemStacks);
    }

}
