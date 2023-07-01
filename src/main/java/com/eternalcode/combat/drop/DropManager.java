package com.eternalcode.combat.drop;

import com.eternalcode.combat.config.implementation.PluginConfig;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DropManager {

    private final Map<DropType, DropModifier> modifiers;

    public DropManager() {
        this.modifiers = new HashMap<>();
    }

    public void registerModifier(DropModifier dropModifier) {
        DropType dropType = dropModifier.getDropType();

        if (dropType == DropType.UNCHANGED) {
            throw new RuntimeException("You cannot register DropModifier for this type '%s'".formatted(dropType.name()));
        }

        this.modifiers.put(dropType, dropModifier);
    }

    public void modify(DropType dropType, DropInfo drop, PluginConfig config) {
        if (!this.modifiers.containsKey(dropType)) {
            throw new RuntimeException("No drop modifier found for type '%s'".formatted(dropType.name()));
        }
        this.modifiers.get(dropType).modifyDrop(drop, config);
    }
}
