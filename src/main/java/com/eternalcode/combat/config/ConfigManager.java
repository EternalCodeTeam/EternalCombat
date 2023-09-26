package com.eternalcode.combat.config;

import com.eternalcode.combat.config.composer.DurationComposer;
import com.eternalcode.combat.config.composer.PotionComposer;
import net.dzikoysk.cdn.Cdn;
import net.dzikoysk.cdn.CdnFactory;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class ConfigManager {

    private final ConfigBackupService backupService;

    private final Cdn cdn = CdnFactory
        .createYamlLike()
        .getSettings()
        .withComposer(Duration.class, new DurationComposer())
        .withComposer(PotionEffect.class, new PotionComposer())
        .build();

    private final Set<ReloadableConfig> configs = new HashSet<>();
    private final File dataFolder;

    public ConfigManager(ConfigBackupService backupService, File dataFolder) {
        this.backupService = backupService;
        this.dataFolder = dataFolder;
    }

    public <T extends ReloadableConfig> T load(T config) {
        this.backupService.createBackup();

        this.cdn.load(config.resource(this.dataFolder), config)
            .orThrow(RuntimeException::new);

        this.cdn.render(config, config.resource(this.dataFolder))
            .orThrow(RuntimeException::new);

        this.configs.add(config);

        return config;
    }

    public void reload() {
        for (ReloadableConfig config : this.configs) {
            this.load(config);
        }
    }

}
