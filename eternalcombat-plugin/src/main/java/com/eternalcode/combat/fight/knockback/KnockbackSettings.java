package com.eternalcode.combat.fight.knockback;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.util.EnumSet;
import org.bukkit.Material;

import java.time.Duration;
import java.util.Set;

public class KnockbackSettings extends OkaeriConfig {

    @Comment({
        "# Horizontal knockback strength multiplier.",
        "# This controls how far the player is pushed horizontally.",
        "#",
        "# Applied in: direction.multiply(multiplier)",
        "# Example: 1.0 ≈ 2–4 blocks push"
    })
    public double multiplier = 1.0;

    @Comment({
        "# Vertical velocity applied when the player is considered on ground.",
        "#",
        "# Used when Y velocity is near zero (< 0.08).",
        "# Higher values = more upward knockback.",
        "# Recommended: 0.15 - 0.3"
    })
    public double vertical = 0.2;

    @Comment({
        "# Force teleport settings for players that fail to be knocked back outside the region.",
        "# If a player cannot be knocked back outside the region after multiple attempts,",
        "# they will be teleported to a safe location outside the region after a short delay.",
        "# This ensures players don't get stuck inside the region due to knockback failures."
    })
    public ForceTeleport forceTeleport = new ForceTeleport();

    public static class ForceTeleport extends OkaeriConfig {

        @Comment({
            "# Delay before force teleport is applied after entering a region.",
            "# Recommended: 1-3 seconds to allow knockback attempts before teleporting.",
        })
        public Duration delay = Duration.ofSeconds(1);

        @Comment({
            "# Blocks that are considered unsafe to stand on when searching for ground.",
            "# These blocks will be ignored during safe ground detection.",
            "#",
            "# Used in findSafeGround():",
            "# - Skips these blocks even if they are solid",
            "# - Prevents teleporting onto dangerous or invalid blocks",
            "#",
            "# Examples:",
            "# - BARRIER (invisible collision)",
            "# - LAVA / WATER (damage / movement issues)",
            "# - CACTUS / MAGMA (damage blocks)",
        })
        public Set<Material> unsafeGroundBlocks = EnumSet.of(
            Material.BARRIER,
            Material.LAVA,
            Material.WATER,
            Material.CACTUS,
            Material.MAGMA_BLOCK,
            Material.FIRE,
            Material.SOUL_FIRE,
            Material.COBWEB,
            Material.SWEET_BERRY_BUSH,
            Material.BEDROCK,
            Material.TNT,
            Material.SEAGRASS,
            Material.TALL_SEAGRASS,
            Material.BUBBLE_COLUMN,
            Material.POWDER_SNOW,
            Material.WITHER_ROSE
        );

        @Comment({
            "# Safe blocks that players can be teleported into",
            "# These blocks don't cause damage and allow free movement",
            "# Players can safely spawn in these blocks or pass through them",
            "# Includes air, grass, flowers, and other non-solid blocks"
        })
        public Set<Material> airBlocks = EnumSet.of(
            Material.AIR,
            Material.CAVE_AIR,
            Material.VOID_AIR,
            Material.TALL_SEAGRASS,
            Material.SEAGRASS,
            Material.GRASS,
            Material.TALL_GRASS,
            Material.VINE,
            Material.STRUCTURE_VOID,
            Material.DEAD_BUSH,
            Material.DANDELION,
            Material.POPPY,
            Material.BLUE_ORCHID,
            Material.ALLIUM,
            Material.AZURE_BLUET,
            Material.RED_TULIP,
            Material.ORANGE_TULIP,
            Material.WHITE_TULIP,
            Material.PINK_TULIP,
            Material.OXEYE_DAISY,
            Material.CORNFLOWER,
            Material.LILY_OF_THE_VALLEY,
            Material.SUNFLOWER,
            Material.LILAC,
            Material.ROSE_BUSH,
            Material.PEONY,
            Material.WITHER_ROSE,
            Material.LARGE_FERN,
            Material.RAIL,
            Material.POWERED_RAIL,
            Material.DETECTOR_RAIL,
            Material.ACTIVATOR_RAIL,
            Material.REDSTONE_WIRE,
            Material.COMPARATOR,
            Material.REPEATER,
            Material.LEVER,
            Material.STRING,
            Material.SNOW,
            Material.CRIMSON_ROOTS,
            Material.WARPED_ROOTS,
            Material.CRIMSON_FUNGUS,
            Material.WARPED_FUNGUS,
            Material.TORCH,
            Material.WALL_TORCH,
            Material.REDSTONE_TORCH,
            Material.REDSTONE_WALL_TORCH,
            Material.SOUL_TORCH,
            Material.SOUL_WALL_TORCH
        );

    }


}
