package com.eternalcode.combat.fight.knockback;

import com.cryptomorin.xseries.XMaterial;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.time.Duration;
import java.util.EnumSet;
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
        public Set<XMaterial> unsafeGroundBlocks = EnumSet.of(
            XMaterial.BARRIER,
            XMaterial.LAVA,
            XMaterial.WATER,
            XMaterial.CACTUS,
            XMaterial.MAGMA_BLOCK,
            XMaterial.FIRE,
            XMaterial.SOUL_FIRE,
            XMaterial.COBWEB,
            XMaterial.SWEET_BERRY_BUSH,
            XMaterial.BEDROCK,
            XMaterial.TNT,
            XMaterial.SEAGRASS,
            XMaterial.TALL_SEAGRASS,
            XMaterial.BUBBLE_COLUMN,
            XMaterial.POWDER_SNOW,
            XMaterial.WITHER_ROSE
        );

        @Comment({
            "# Safe blocks that players can be teleported into",
            "# These blocks don't cause damage and allow free movement",
            "# Players can safely spawn in these blocks or pass through them",
            "# Includes air, grass, flowers, and other non-solid blocks"
        })
        public Set<XMaterial> airBlocks = EnumSet.of(
            XMaterial.AIR,
            XMaterial.CAVE_AIR,
            XMaterial.VOID_AIR,
            XMaterial.TALL_SEAGRASS,
            XMaterial.SEAGRASS,
            XMaterial.SHORT_GRASS,
            XMaterial.TALL_GRASS,
            XMaterial.VINE,
            XMaterial.STRUCTURE_VOID,
            XMaterial.DEAD_BUSH,
            XMaterial.DANDELION,
            XMaterial.POPPY,
            XMaterial.BLUE_ORCHID,
            XMaterial.ALLIUM,
            XMaterial.AZURE_BLUET,
            XMaterial.RED_TULIP,
            XMaterial.ORANGE_TULIP,
            XMaterial.WHITE_TULIP,
            XMaterial.PINK_TULIP,
            XMaterial.OXEYE_DAISY,
            XMaterial.CORNFLOWER,
            XMaterial.LILY_OF_THE_VALLEY,
            XMaterial.SUNFLOWER,
            XMaterial.LILAC,
            XMaterial.ROSE_BUSH,
            XMaterial.PEONY,
            XMaterial.WITHER_ROSE,
            XMaterial.LARGE_FERN,
            XMaterial.RAIL,
            XMaterial.POWERED_RAIL,
            XMaterial.DETECTOR_RAIL,
            XMaterial.ACTIVATOR_RAIL,
            XMaterial.REDSTONE_WIRE,
            XMaterial.COMPARATOR,
            XMaterial.REPEATER,
            XMaterial.LEVER,
            XMaterial.STRING,
            XMaterial.SNOW,
            XMaterial.CRIMSON_ROOTS,
            XMaterial.WARPED_ROOTS,
            XMaterial.CRIMSON_FUNGUS,
            XMaterial.WARPED_FUNGUS,
            XMaterial.TORCH,
            XMaterial.WALL_TORCH,
            XMaterial.REDSTONE_TORCH,
            XMaterial.REDSTONE_WALL_TORCH,
            XMaterial.SOUL_TORCH,
            XMaterial.SOUL_WALL_TORCH
        );

    }


}
