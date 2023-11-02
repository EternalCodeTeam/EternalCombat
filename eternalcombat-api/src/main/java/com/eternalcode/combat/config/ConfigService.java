package com.eternalcode.combat.config;

import com.eternalcode.combat.notification.serializer.NotificationSerializer;
import com.eternalcode.combat.notification.implementation.title.serializer.TitleTimesSerializer;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ConfigService {

    private final ConfigBackupService backupService;

    private final Set<OkaeriConfig> configs = new HashSet<>();

    public ConfigService(ConfigBackupService backupService) {
        this.backupService = backupService;
    }

    public <T extends OkaeriConfig> T create(Class<T> config, File file) {
        this.backupService.createBackup();

        T configFile = ConfigManager.create(config);

        configFile.withConfigurer(new YamlBukkitConfigurer(), new SerdesCommons(), new SerdesBukkit());
        configFile.withSerdesPack(registry -> {
            registry.register(new NotificationSerializer());
            registry.register(new TitleTimesSerializer());
        });

        configFile.withBindFile(file);
        configFile.withRemoveOrphans(true);
        configFile.saveDefaults();
        configFile.load(true);

        this.configs.add(configFile);

        return configFile;
    }

    public void reload() {
        for (OkaeriConfig config : this.configs) {
            config.load();
        }
    }
}
