package com.eternalcode.combat.drop.impl;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.drop.DropModifier;
import com.eternalcode.combat.drop.Drop;
import com.eternalcode.combat.drop.DropSettings;
import com.eternalcode.combat.drop.DropType;
import com.eternalcode.combat.util.CollectionsUtil;
import com.eternalcode.combat.util.MathUtil;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PercentDropModifier implements DropModifier {

    private final DropSettings settings;

    public PercentDropModifier(DropSettings settings) {
        this.settings = settings;
    }

    @Override
    public DropType getDropType() {
        return DropType.PERCENT;
    }

    @Override
    public void modifyDrop(Drop drop) {
        int dropItemPercent = 100 - MathUtil.clamp(this.settings.dropItemPercent, 0, 100);

        List<ItemStack> droppedItems = drop.getDroppedItems();

        int itemsToDelete = CollectionsUtil.calculateItemsToDelete(dropItemPercent, droppedItems, ItemStack::getAmount);
        int droppedExp = MathUtil.getRoundedCountFromPercentage(dropItemPercent, drop.getDroppedExp());

        CollectionsUtil.removeRandomItems(droppedItems, itemsToDelete);

        drop.setDroppedItems(droppedItems);
        drop.setDroppedExp(droppedExp);
    }
}
