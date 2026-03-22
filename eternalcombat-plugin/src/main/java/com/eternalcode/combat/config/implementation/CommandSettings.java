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
}
