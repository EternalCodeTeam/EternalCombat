package com.eternalcode.combat.util;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.ToIntFunction;

public class InventoryUtil {

    private static final Random RANDOM = new Random();

    private InventoryUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static <T> int calculateItemsToDelete(int percent, Collection<T> objectList, ToIntFunction<? super T> mapper) {
        return MathUtil.getRoundedCountFromPercentage(percent, MathUtil.sum(objectList, mapper));
    }

    public static RemoveItemResult removeRandomItems(List<ItemStack> list, int itemsToDelete) {
        List<ItemStack> currentItems = new ArrayList<>(list);
        List<ItemStack> removedItems = new ArrayList<>();

        int totalAvailable = 0;
        for (ItemStack item : currentItems) {
            totalAvailable += Math.max(0, item.getAmount());
        }

        // Never try to remove more than is actually available, otherwise the loop
        // could never reach zero and would spin forever.
        int currentItemsToDelete = Math.min(itemsToDelete, totalAvailable);

        while (currentItemsToDelete > 0 && !currentItems.isEmpty()) {
            int randomIndex = RANDOM.nextInt(currentItems.size());
            ItemStack currentItem = currentItems.get(randomIndex);

            int amount = currentItem.getAmount();
            if (amount <= 0) {
                currentItems.remove(randomIndex);
                continue;
            }

            int maxRemovable = Math.min(currentItemsToDelete, amount);
            int randomAmount = RANDOM.nextInt(1, maxRemovable + 1);

            ItemStack removedItem = currentItem.clone();
            removedItem.setAmount(randomAmount);
            removedItems.add(removedItem);

            int remaining = amount - randomAmount;
            if (remaining <= 0) {
                currentItems.remove(randomIndex);
            } else {
                currentItem.setAmount(remaining);
            }

            currentItemsToDelete -= randomAmount;
        }

        return new RemoveItemResult(currentItems, removedItems);
    }

}
