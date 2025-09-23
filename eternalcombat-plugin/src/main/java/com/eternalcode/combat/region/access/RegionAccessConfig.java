package com.eternalcode.combat.region.access;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class RegionAccessConfig extends OkaeriConfig {

    @Comment("Enable wartime region access bypass")
    public boolean wartimeEnabled = true;

    @Comment("Default behavior when no provider is available")
    public boolean defaultAllow = false;

}

