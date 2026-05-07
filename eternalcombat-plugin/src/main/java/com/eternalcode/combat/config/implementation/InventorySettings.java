package com.eternalcode.combat.config.implementation;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;

public class InventorySettings extends OkaeriConfig {

    @Comment({
        "# Inventory access restriction mode during combat",
        "# ALLOW_ALL - Players can open all inventories during combat",
        "# BLOCK_ALL - Players cannot open any inventories during combat",
        "# BLACKLIST - Block only inventories listed in restrictedInventoryTypes",
        "# WHITELIST - Allow only inventories listed in restrictedInventoryTypes"
    })
    public InventoryAccessMode inventoryAccessMode = InventoryAccessMode.ALLOW_ALL;

    @Comment({
        "# List of inventory types to restrict or allow (depending on the mode)",
        "# In BLACKLIST mode: these inventories will be blocked",
        "# In WHITELIST mode: only these inventories will be allowed",
        "# Available types: CHEST, ENDER_CHEST, DISPENSER, DROPPER, FURNACE, WORKBENCH,",
        "# CRAFTING, ENCHANTING, BREWING, MERCHANT, BEACON, ANVIL, SMITHING, HOPPER,",
        "# SHULKER_BOX, BARREL, BLAST_FURNACE, LECTERN, SMOKER, LOOM, CARTOGRAPHY,",
        "# GRINDSTONE, STONECUTTER, COMPOSTER, CHISELED_BOOKSHELF, JUKEBOX, SMITHING_NEW,",
        "# CRAFTER, DECORATED_POT",
        "# Note: PLAYER and CREATIVE inventories are never blocked to prevent game-breaking issues",
        "# Full list: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/InventoryType.html"
    })
    public List<InventoryType> restrictedInventoryTypes = List.of(
        InventoryType.CHEST,
        InventoryType.ENDER_CHEST,
        InventoryType.BARREL,
        InventoryType.SHULKER_BOX
    );
}
