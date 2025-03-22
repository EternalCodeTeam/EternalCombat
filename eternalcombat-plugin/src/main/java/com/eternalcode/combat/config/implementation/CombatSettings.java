package com.eternalcode.combat.config.implementation;

import com.eternalcode.combat.WhitelistBlacklistMode;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.util.List;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class CombatSettings extends OkaeriConfig {
    @Comment({
        "# Automatically release the attacker from combat when the victim dies.",
        "# Set to 'true' to enable this feature, or 'false' to keep the attacker in combat."
    })
    public boolean releaseAttackerOnVictimDeath = true;

    @Comment({
        "# Disable the use of elytra during combat.",
        "# Set to 'true' to prevent players from using elytra while in combat."
    })
    public boolean disableElytraUsage = true;

    @Comment({
        "# Disable the use of elytra when a player takes damage.",
        "# Set to 'true' to disable elytra usage upon taking damage, even when the player is mid-air."
    })
    public boolean disableElytraOnDamage = true;

    @Comment({
        "# Prevent players from flying during combat.",
        "# Flying players will fall to the ground if this is enabled."
    })
    public boolean disableFlying = true;

    @Comment({
        "# Prevent players from opening their inventory during combat.",
        "# Set to 'true' to block inventory access while in combat."
    })
    public boolean disableInventoryAccess = false;

    @Comment({
        "# Enable or disable combat logging for damage caused by non-player entities.",
        "# Set to 'true' to log damage from non-player sources, or 'false' to disable this feature."
    })
    public boolean enableDamageCauseLogging = false;

    @Comment({
        "# Set the mode for logging damage causes.",
        "# Available modes: WHITELIST (only listed causes are logged), BLACKLIST (all causes except listed ones are logged)."
    })
    public WhitelistBlacklistMode damageCauseRestrictionMode = WhitelistBlacklistMode.WHITELIST;

    @Comment({
        "# List of damage causes to be logged based on the selected mode.",
        "# In WHITELIST mode, only these causes are logged. In BLACKLIST mode, all causes except these are logged.",
        "# For a full list of damage causes, visit: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html"
    })
    public List<DamageCause> loggedDamageCauses = List.of(
        DamageCause.LAVA,
        DamageCause.CONTACT,
        DamageCause.FIRE,
        DamageCause.FIRE_TICK
    );

    @Comment({
        "# List of projectile types that do not trigger combat tagging.",
        "# Players hit by these projectiles will not be tagged as in combat.",
        "# For a full list of entity types, visit: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html"
    })
    public List<EntityType> ignoredProjectileTypes = List.of(
        EntityType.ENDER_PEARL,
        EntityType.EGG
    );
}
