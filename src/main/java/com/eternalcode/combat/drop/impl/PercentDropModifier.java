package com.eternalcode.combat.drop.impl;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.drop.DropModifier;
import com.eternalcode.combat.drop.Drop;
import com.eternalcode.combat.drop.DropType;
import com.eternalcode.combat.util.MathUtil;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class PercentDropModifier implements DropModifier {

    private static final Random RANDOM = new Random();

    private final PluginConfig config;

    public PercentDropModifier(PluginConfig config) {
        this.config = config;
    }

    @Override
    public DropType getDropType() {
        return DropType.PERCENT;
    }

    @Override
    public void modifyDrop(Drop drop) {
        int dropItemPercent = 100 - MathUtil.clamp(config.dropSettings.dropItemPercent, 0, 100);

        List<ItemStack> droppedItems = drop.getDroppedItems();

        int itemsToDelete = this.calculateItemsToDelete(dropItemPercent, droppedItems);
        int droppedExp = this.calculateDroppedExp(dropItemPercent, drop.getDroppedExp());

        for (int i = 0; i < itemsToDelete; i++) {
            if (droppedItems.isEmpty()) {
                break;
            }

            int index = RANDOM.nextInt(droppedItems.size());

            ItemStack item = droppedItems.get(index);
            int newAmount = item.getAmount() - 1;

            if (newAmount <= 0) {
                droppedItems.remove(index);
                continue;
            }

            item.setAmount(newAmount);
        }

        drop.setDroppedItems(droppedItems);
        drop.setDroppedExp(droppedExp);
    }

    private int calculateItemsToDelete(int percent, List<ItemStack> droppedItems) {
        int total = MathUtil.sum(droppedItems, ItemStack::getAmount);
        return MathUtil.getRoundedCountFromPercentage(percent, total);
    }

    private int calculateDroppedExp(int percent, int exp) {
        return MathUtil.getRoundedCountFromPercentage(percent, exp);
    }
}
