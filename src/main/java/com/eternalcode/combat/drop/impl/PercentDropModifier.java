package com.eternalcode.combat.drop.impl;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.drop.DropModifier;
import com.eternalcode.combat.drop.DropInfo;
import com.eternalcode.combat.drop.DropType;
import com.eternalcode.combat.util.MathUtil;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class PercentDropModifier implements DropModifier {

    @Override
    public DropType getDropType() {
        return DropType.PERCENT;
    }

    @Override
    public List<ItemStack> modifyDrop(DropInfo info, PluginConfig config) {
        Random random = new Random();

        int dropItemPercent = config.settings.dropItemPercent;

        List<ItemStack> droppedItems = info.getDroppedItems();

        int totalItemsSize = droppedItems.stream().mapToInt(ItemStack::getAmount).sum();
        long amountItemsToDelete = MathUtil.calculateRoundedPercentageValue(dropItemPercent, totalItemsSize);

        for (int i = 0; i < amountItemsToDelete; i++) {
            int index = random.nextInt(droppedItems.size());

            ItemStack item = droppedItems.get(index);
            int newAmount = item.getAmount() - 1;

            if (newAmount <= 0) {
                droppedItems.remove(index);
                continue;
            }

            item.setAmount(newAmount);
        }

        return droppedItems;
    }
}
