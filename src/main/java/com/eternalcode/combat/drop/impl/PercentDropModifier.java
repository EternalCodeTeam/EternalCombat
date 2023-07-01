package com.eternalcode.combat.drop.impl;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.drop.DropModifier;
import com.eternalcode.combat.drop.DropInfo;
import com.eternalcode.combat.drop.DropType;
import com.eternalcode.combat.util.MathUtil;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PercentDropModifier implements DropModifier {

    @Override
    public DropType getDropType() {
        return DropType.PERCENT;
    }

    @Override
    public List<ItemStack> modifyDrop(DropInfo record, PluginConfig config) {
        Random random = new Random();

        int percent = config.settings.dropItemPercent;

        List<ItemStack> originalDrop = record.getDroppedItems();

        int originalSize = originalDrop.stream()
            .mapToInt(ItemStack::getAmount)
            .sum();

        long modifiedSize = MathUtil.getRoundedFromPercent(percent, originalSize);

        List<ItemStack> modifiedDrop = new ArrayList<>(List.copyOf(originalDrop));

        for (int i = 0; i < modifiedSize; i++) {
            int randomIndex = random.nextInt(originalDrop.size());

            ItemStack item = modifiedDrop.get(randomIndex).clone();
            int ammount = item.getAmount();

            item.setAmount(ammount - 1);

            if (ammount <= 0) {
                modifiedDrop.remove(randomIndex);
                continue;
            }

            modifiedDrop.set(randomIndex, item);
        }

        return modifiedDrop;
    }
}
