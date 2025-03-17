package com.eternalcode.combat.config.implementation;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class AdminSettings extends OkaeriConfig {
    @Comment({
        "# Exclude server administrators from combat tagging and being tagged.",
        "# Set to 'true' to prevent admins from being tagged or tagging others.",
        "# Set to 'false' to allow admins to participate in combat."
    })
    public boolean excludeAdminsFromCombat = false;

    @Comment({
        "# Exclude players in creative mode from combat tagging and being tagged.",
        "# Set to 'true' to prevent creative mode players from being tagged or tagging others.",
        "# Set to 'false' to allow creative mode players to participate in combat."
    })
    public boolean excludeCreativePlayersFromCombat = false;
}
