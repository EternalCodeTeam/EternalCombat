package com.eternalcode.combat.fight.knockback;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
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
        "# Maximum vertical velocity when player is already airborne.",
        "#",
        "# Prevents stacking vertical velocity or launching too high.",
        "# Used as: min(currentY, maxAirVertical)"
    })
    public double maxAirVertical = 0.2;

    @Comment({
        "# Delay before force teleport is applied after entering a region.",
        "#",
        "# Used in forceKnockbackLater().",
        "# Prevents instant teleport when crossing region borders."
    })
    public Duration forceDelay = Duration.ofSeconds(1);

    @Comment({
        "# Enables teleport fallback after knockback.",
        "#",
        "# If knockback does not push the player outside the region,",
        "# a safe location will be generated and player will be teleported."
    })
    public boolean useTeleport = false;

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
    public Set<Material> unsafeGroundBlocks = Set.of(
        Material.BARRIER,
        Material.LAVA,
        Material.WATER,
        Material.CACTUS,
        Material.MAGMA_BLOCK,
        Material.FIRE,
        Material.SOUL_FIRE
    );

    @Comment({
        "# Use custom safe ground detection instead of highest block. (recommended)",
        "#",
        "# true  -> scans downward for safe landing",
        "# false -> uses Bukkit getHighestBlockAt()",
        "#",
        "# Safe mode prevents:",
        "# - Landing on roofs",
        "# - Landing on barriers",
        "# - Unsafe teleport positions"
    })
    public boolean safeGroundCheck = true;

    @Comment({
        "# Enables safe fallback scanning instead of using Bukkit highest block directly.",
        "#",
        "# true  -> scans downward from highest block to find safe ground",
        "# false -> uses Bukkit getHighestBlockAt (faster but unsafe)"
    })
    public boolean safeHighestFallback = true;

    @Comment({
        "# Maximum vertical scan distance for safe highest fallback.",
        "#",
        "# Prevents excessive scanning in very tall worlds.",
        "# Set to -1 to scan all the way down to min world height."
    })
    public int safeHighestMaxScan = -1;

    @Comment({
        "# If true, prevents teleport if no safe ground is found at all.",
        "#",
        "# true  -> cancel teleport",
        "# false -> fallback to original location"
    })
    public boolean cancelIfNoSafeGround = false;

    @Comment({
        "# Y offset added after finding ground.",
        "#",
        "# Usually 1.0 = player stands exactly on block.",
        "# Can be increased slightly to prevent clipping issues."
    })
    public double groundOffset = 1.0;

    @Comment({
        "# Reduces player's current velocity BEFORE applying knockback.",
        "#",
        "# Helps create smoother and more consistent knockback.",
        "# Prevents stacking velocity from previous movement."
    })
    public boolean dampenVelocity = true;

    @Comment({
        "# Multiplier applied when dampening velocity.",
        "#",
        "# 1.0 = no change",
        "# 0.0 = completely stop player",
        "# Recommended: 0.6 - 0.9"
    })
    public double dampenFactor = 0.8;

    @Comment({
        "# Maximum recursive attempts when generating a safe location.",
        "#",
        "# Used in generate() method.",
        "# Prevents infinite loops when regions overlap or chain.",
        "#",
        "# If exceeded -> fallback to player current location."
    })
    public int maxAttempts = 5;
}
