package com.eternalcode.combat.config.implementation;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.util.List;
import org.bukkit.Material;

public class BlockPlacementSettings extends OkaeriConfig {
    @Comment({
        "# Prevent players from placing blocks during combat.",
        "# Set to 'true' to block block placement while in combat."
    })
    public boolean disableBlockPlacing = true;

    @Comment({
        "# Restrict block placement above or below a specific Y coordinate.",
        "# Available modes: ABOVE (blocks cannot be placed above the Y coordinate), BELOW (blocks cannot be placed below the Y coordinate)."
    })
    public BlockPlacingMode blockPlacementMode = BlockPlacingMode.ABOVE;

    @Comment({
        "# Custom name for the block placement mode used in messages.",
        "# This name will be displayed in notifications related to block placement restrictions."
    })
    public String blockPlacementModeDisplayName = "above";

    @Comment({
        "# Define the Y coordinate for block placement restrictions.",
        "# This value is relative to the selected block placement mode (ABOVE or BELOW)."
    })
    public int blockPlacementYCoordinate = 40;
    @Comment({
        "# Restrict the placement of specific blocks during combat.",
        "# Add blocks to this list to prevent their placement. Leave the list empty to block all blocks.",
        "# Note: This feature requires 'disableBlockPlacing' to be enabled."
    })
    public List<Material> restrictedBlockTypes = List.of();

    public enum BlockPlacingMode {
        ABOVE,
        BELOW
    }
}
