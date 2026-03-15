package com.eternalcode.combat.config.implementation;

import com.eternalcode.combat.WhitelistBlacklistMode;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.util.List;

public class CommandSettings extends OkaeriConfig {
    @Comment({
        "# Set the mode for command restrictions during combat.",
        "# Available modes: WHITELIST (only listed commands are allowed), BLACKLIST (listed commands are blocked)."
    })
    public WhitelistBlacklistMode commandRestrictionMode = WhitelistBlacklistMode.BLACKLIST;

    @Comment({
        "# List of commands affected by the command restriction mode.",
        "# In BLACKLIST mode, these commands are blocked. In WHITELIST mode, only these commands are allowed."
    })
    public List<String> restrictedCommands = List.of(
        "gamemode",
        "spawn",
        "tp",
        "tpa",
        "tpaccept"
    );

    public PostDeathSettings onDeathInCombat = new PostDeathSettings();

    public PostDeathSettings onAnyDeath = new PostDeathSettings();

    public PostDeathSettings afterRespawn = new PostDeathSettings();

    public PostDeathSettings onUntag = new PostDeathSettings();

    public static class PostDeathSettings extends OkaeriConfig {
        public List<String> console = List.of();
        public List<String> player = List.of();
    }

    @Comment({
        "# List of commands that will be executed from the killer's perspective after killing a player.",
        "# Use {player} to represent the name of the player who was killed and {killer} for the killer's name (if applicable)."
    })
    public List<String> killerPostDeathCommands = List.of(
        "say You have killed {player} in combat!"
    );

    @Comment("# The returned string when the killer is unknown")
    public String unknownKillerPlaceholder = "Unknown";
}
