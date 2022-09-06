package com.eripe14.combatlog.config.implementation;

import com.eripe14.combatlog.config.ReloadableConfig;
import com.google.common.collect.ImmutableList;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;

import java.io.File;
import java.util.List;

public class PluginConfig implements ReloadableConfig {

    public int combatLogTime = 20;

    public List<String> blockedCommands = new ImmutableList.Builder<String>()
            .add("gamemode")
            .add("tp")
            .build();

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "config.yml");
    }
}
