package com.eternalcode.combatlog.config.implementation;

import com.eternalcode.combatlog.config.ReloadableConfig;
import com.google.common.collect.ImmutableList;
import net.dzikoysk.cdn.entity.Description;
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

    @Description("# Czy blokować otwieranie inventory?")
    public boolean blockingInventories = true;

    @Description("# Czy blokować stawianie bloków?")
    public boolean blockPlace = true;

    @Description("# Od którego poziomu blokować stawianie bloków?")
    public int blockPlaceLevel = 40;

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "config.yml");
    }
}
