package com.eternalcode.combat.config;

import com.eternalcode.combat.border.animation.block.BlockTypeTransformer;
import com.eternalcode.combat.border.animation.particle.ParticleColorTransformer;
import com.eternalcode.combat.border.animation.particle.ParticleTypeTransformer;
import com.eternalcode.combat.notification.serializer.NotificationSerializer;
import com.eternalcode.multification.bukkit.notice.resolver.sound.SoundBukkitResolver;
import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.notice.resolver.NoticeResolverRegistry;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ConfigService {

    private final Set<OkaeriConfig> configs = new HashSet<>();

    public <T extends OkaeriConfig> T create(Class<T> config, File file) {
        T configFile = ConfigManager.create(config);

        YamlBukkitConfigurer yamlBukkitConfigurer = new YamlBukkitConfigurer();
        NoticeResolverRegistry noticeRegistry = NoticeResolverDefaults.createRegistry()
            .registerResolver(new SoundBukkitResolver());

        configFile.withConfigurer(yamlBukkitConfigurer,
            new SerdesCommons(),
            new SerdesBukkit(),
            new MultificationSerdesPack(noticeRegistry),
            new DefaultSerdesPack()
        );
        configFile.withSerdesPack(registry -> registry.register(new NotificationSerializer()));

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

    private static class DefaultSerdesPack implements OkaeriSerdesPack {
        @Override
        public void register(SerdesRegistry config) {
            config.register(new ParticleColorTransformer());
            config.register(new ParticleTypeTransformer());
            config.register(new BlockTypeTransformer());
        }
    }
}
