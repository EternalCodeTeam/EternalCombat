package com.eternalcode.combat.drop.impl;

import com.eternalcode.combat.drop.Drop;
import com.eternalcode.combat.drop.DropModifier;
import com.eternalcode.combat.drop.DropSettings;
import com.eternalcode.combat.drop.DropType;
import com.eternalcode.combat.fight.FightDeathCause;
import com.eternalcode.combat.fight.FightTag;
import com.eternalcode.combat.util.InventoryUtil;
import com.eternalcode.combat.util.MathUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayersHealthDropModifier implements DropModifier {

    private final DropSettings settings;

    public PlayersHealthDropModifier(DropSettings settings) {
        this.settings = settings;
    }

    @Override
    public DropType getDropType() {
        return DropType.PLAYERS_HEALTH;
    }

    @Override
    public void modifyDrop(Drop drop) {
        if (drop.getDeathCause() != FightDeathCause.ESCAPE) {
            return;
        }

        Player player = drop.getPlayer();
        FightTag fightTag = drop.getFightTag();

        List<ItemStack> droppedItems = drop.getDroppedItems();

        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        double health = fightTag.getHealthBeforeDeath();

        int percentHealth = MathUtil.getRoundedCountPercentage(health, maxHealth);
        int reversedPercent = MathUtil.clamp(100 - percentHealth, this.settings.playersHealthPercentClamp, 100);

        int itemsToDelete = InventoryUtil.calculateItemsToDelete(reversedPercent, droppedItems, ItemStack::getAmount);
        int droppedExp = MathUtil.getRoundedCountFromPercentage(reversedPercent, drop.getDroppedExp());

        InventoryUtil.removeRandomItems(droppedItems, itemsToDelete);

        drop.setDroppedItems(droppedItems);
        drop.setDroppedExp(droppedExp);
    }
}
