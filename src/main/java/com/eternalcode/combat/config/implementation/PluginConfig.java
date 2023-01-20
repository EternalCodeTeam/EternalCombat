package com.eternalcode.combat.config.implementation;

import com.eternalcode.combat.config.ReloadableConfig;
import com.google.common.collect.ImmutableList;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;

import java.io.File;
import java.time.Duration;
import java.util.List;

public class PluginConfig implements ReloadableConfig {

    @Description("# The length of time the combat is to last")
    public Duration combatLogTime = Duration.ofSeconds(20);

    @Description({ " ", "# Blocked commands that the player will not be able to use during combat" })
    public List<String> blockedCommands = new ImmutableList.Builder<String>()
            .add("gamemode")
            .add("tp")
            .build();

    @Description({ " ", "# Block the opening of inventory??" })
    public boolean blockingInventories = true;

    @Description({ " ", "# Whether to block the placement of blocks?" })
    public boolean blockPlace = true;

    @Description({ " ", "# From which level should place blocks be blocked?" })
    public int blockPlaceLevel = 40;

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "config.yml");
    }
}
