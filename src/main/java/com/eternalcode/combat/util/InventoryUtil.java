package com.eternalcode.combat.util;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        List<ItemStack> restItems = new ArrayList<>(list);
        Map<Integer, ItemStack> removedItems = new HashMap<>();

        for (int i = 0; i < itemsToDelete; i++) {
            if (restItems.isEmpty()) {
                break;
            }

            int randomIndex = RANDOM.nextInt(restItems.size());

            ItemStack item = restItems.get(randomIndex);
            int newAmount = item.getAmount() - 1;

            if (newAmount <= 0) {
                restItems.remove(randomIndex);
                incrementItem(removedItems, randomIndex, item);
                continue;
            }

            item.setAmount(newAmount);
            incrementItem(removedItems, randomIndex, item);
        }

        return new RemoveItemResult(restItems, new ArrayList<>(removedItems.values()));
    }

    // TODO: kinda shitty, but no time for a proper solution now
    private static void incrementItem(Map<Integer, ItemStack> items, int index, ItemStack itemStack) {
        ItemStack itemInList = items.get(index);

        if (itemInList == null) {
            ItemStack cloned = itemStack.clone();

            cloned.setAmount(1);
            items.put(index, cloned);
            return;
        }

        if (!itemInList.isSimilar(itemStack)) {
            throw new IllegalStateException("Items are not similar!");
        }

        itemInList.setAmount(itemInList.getAmount() + 1);
    }

}
