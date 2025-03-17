package com.eternalcode.combat.config.implementation;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.util.Collections;
import java.util.List;

public class RegionSettings extends OkaeriConfig {
    @Comment({
        "# List of regions where combat is restricted.",
        "# Players in these regions will not be able to engage in combat."
    })
    public List<String> blockedRegions = Collections.singletonList("your_region");

    @Comment({
        "# Prevent players from entering regions where PVP is disabled by WorldGuard.",
        "# Set to 'true' to enforce this restriction, or 'false' to allow PVP in all regions."
    })
    public boolean preventPvpInRegions = true;

    @Comment({
        "# Define the radius of restricted regions if WorldGuard is not used.",
        "# This setting is based on the default spawn region."
    })
    public int restrictedRegionRadius = 10;
}
