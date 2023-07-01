package com.eternalcode.combat.drop;

import com.eternalcode.combat.config.implementation.PluginConfig;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface DropModifier {

    DropType getDropType();

    void modifyDrop(DropInfo info, PluginConfig config);

}
