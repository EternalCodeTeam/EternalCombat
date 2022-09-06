package com.eripe14.combatlog.config;

import com.eripe14.combatlog.config.composer.DurationComposer;
import net.dzikoysk.cdn.Cdn;
import net.dzikoysk.cdn.CdnFactory;

import java.io.File;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class ConfigManager {

    private final Cdn cdn = CdnFactory
            .createYamlLike()
            .getSettings()
            .withComposer(Duration.class, new DurationComposer())
            .build();

    private final Set<ReloadableConfig> configs = new HashSet<>();
    private final File dataFolder;

    public ConfigManager(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    public <T extends ReloadableConfig> T load(T config) {
        cdn.load(config.resource(this.dataFolder), config)
                .orThrow(RuntimeException::new);

        cdn.render(config, config.resource(this.dataFolder))
                .orThrow(RuntimeException::new);

        this.configs.add(config);

        return config;
    }

    public void reload() {
        for (ReloadableConfig config : this.configs) {
            load(config);
        }
    }

}
