package com.eripe14.combatlog.config;


import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;

import java.io.File;

public class ConfigLoader {

    private final File dataFile;

    public ConfigLoader(File dataFile) {
        this.dataFile = dataFile;
    }

    public PluginConfig loadPluginConfig() {
        return ConfigManager.create(PluginConfig.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            it.withBindFile(this.dataFile);
            it.saveDefaults();
            it.load(true);
        });
    }

    public MessageConfig loadMessageConfig() {
        return ConfigManager.create(MessageConfig.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            it.withBindFile(this.dataFile);
            it.saveDefaults();
            it.load(true);
        });
    }

}
