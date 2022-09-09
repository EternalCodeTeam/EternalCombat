package com.eternalcode.combatlog.config.implementation;

import com.eternalcode.combatlog.config.ReloadableConfig;
import com.google.common.collect.ImmutableList;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;

import java.io.File;
import java.time.Duration;
import java.util.List;

public class PluginConfig implements ReloadableConfig {

    public Duration combatLogTime = Duration.ofSeconds(20);

    public List<String> blockedCommands = new ImmutableList.Builder<String>()
            .add("gamemode")
            .add("tp")
            .build();

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "config.yml");
    }
}
