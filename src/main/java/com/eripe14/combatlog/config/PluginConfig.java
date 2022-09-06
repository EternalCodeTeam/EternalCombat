package com.eripe14.combatlog.config;

import com.google.common.collect.ImmutableList;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;

import java.util.List;

@Header({"### EternalCombatLog (Main-Config) ###"})
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class PluginConfig extends OkaeriConfig {

    public int combatLogTime = 20;

    public List<String> blockedCommands = new ImmutableList.Builder<String>()
            .add("gamemode")
            .add("tp")
            .build();

}
