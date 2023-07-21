package com.eternalcode.combat.util;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.ToIntFunction;

public class CollectionsUtil {

    private static final Random RANDOM = new Random();

    private CollectionsUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static <T> int calculateItemsToDelete(int percent, Collection<T> objectList, ToIntFunction<? super T> mapper) {
        return MathUtil.getRoundedCountFromPercentage(percent, MathUtil.sum(objectList, mapper));
    }

    public static void removeRandomItems(List<ItemStack> list, int itemsToDelete) {
        for (int i = 0; i < itemsToDelete; i++) {
            if (list.isEmpty()) {
                break;
            }

            int index = RANDOM.nextInt(list.size());

            ItemStack item = list.get(index);
            int newAmount = item.getAmount() - 1;

            if (newAmount <= 0) {
                list.remove(index);
                continue;
            }

            item.setAmount(newAmount);
        }
    }
}
