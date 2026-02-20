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

    @Comment({
        "# List of commands that will be executed from console after player death.",
        "# Use {dead} to represent the name of the player who died and {killer} for the killer's name (if applicable)."
    })
    public List<String> consolePostDeathCommands = List.of(
        "broadcast {player} has died in combat!"
    );


    @Comment({
        "# List of commands that will be executed from the dead player's perspective after death.",
        "# Use {player} to represent the name of the player who died and {killer} for the killer's name (if applicable)."
    })
    public List<String> deadPostDeathCommands = List.of(
        "say You have died in combat!"
    );

    @Comment("# When this is set to true, the plugin will execute the commands above only after the dead player has respawned.")
    public boolean executeAfterRespawn = true;


    @Comment({
        "# List of commands that will be executed from the killer's perspective after killing a player.",
        "# Use {player} to represent the name of the player who was killed and {killer} for the killer's name (if applicable)."
    })
    public List<String> killerPostDeathCommands = List.of(
        "say You have killed {player} in combat!"
    );

    @Comment("# When this is set to true, the plugin will only execute the post-death commands if the players were tagged")
    public boolean onlyExecuteIfTagged = true;
}
