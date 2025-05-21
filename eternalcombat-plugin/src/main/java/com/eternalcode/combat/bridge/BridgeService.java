package com.eternalcode.combat.bridge;

import com.eternalcode.combat.bridge.lands.LandsBridgeInitializer;
import com.eternalcode.combat.bridge.placeholder.FightTagPlaceholder;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.event.EventManager;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.region.CompositeRegionProvider;
import com.eternalcode.combat.region.DefaultRegionProvider;
import com.eternalcode.combat.region.RegionProvider;
import com.eternalcode.combat.region.WorldGuardRegionProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class BridgeService {

    private final PluginConfig pluginConfig;
    private final PluginManager pluginManager;
    private final Logger logger;
    private final Plugin plugin;
    private final EventManager eventManager;
    private final FightManager fightManager;
    private final NoticeService noticeService;

    private RegionProvider regionProvider;

    public BridgeService(
        PluginConfig pluginConfig,
        PluginManager pluginManager,
        Logger logger,
        Plugin plugin,
        EventManager eventManager,
        FightManager fightManager,
        NoticeService noticeService
    ) {
        this.pluginConfig = pluginConfig;
        this.pluginManager = pluginManager;
        this.logger = logger;
        this.plugin = plugin;
        this.eventManager = eventManager;
        this.fightManager = fightManager;
        this.noticeService = noticeService;
    }

    public void init(Server server) {
        List<RegionProvider> providers = new ArrayList<>();

        this.initialize(
            "Lands",
            () -> {
                LandsBridgeInitializer landsInit = new LandsBridgeInitializer(
                    this.plugin, this.eventManager, this.fightManager, this.noticeService
                );
                landsInit.initialize();
                providers.add(landsInit.getRegionProvider());
            },
            () -> this.logger.warning("Lands not found; skipping LandsRegionProvider.")
        );

        this.initialize(
            "WorldGuard",
            () -> {
                WorldGuardRegionProvider wg = new WorldGuardRegionProvider(
                    this.pluginConfig.regions.blockedRegions,
                    this.pluginConfig
                );
                providers.add(wg);
            },
            () -> this.logger.warning("WorldGuard not found; skipping WorldGuardRegionProvider.")
        );

        if (providers.isEmpty()) {
            providers.add(new DefaultRegionProvider(this.pluginConfig.regions.restrictedRegionRadius));
            this.logger.warning("No region plugin found; using DefaultRegionProvider.");
        }

        this.regionProvider = new CompositeRegionProvider(providers);
        this.logger.info("Using composite region provider with: " +
            providers.stream()
                .map(p -> p.getClass().getSimpleName())
                .collect(Collectors.joining(", "))
        );

        initialize(
            "PlaceholderAPI",
            () -> new FightTagPlaceholder(this.fightManager, server, this.plugin).register(),
            () -> this.logger.warning("PlaceholderAPI not found; skipping placeholders.")
        );
    }

    private void initialize(String pluginName, BridgeInitializer init, Runnable onFailure) {
        if (this.pluginManager.isPluginEnabled(pluginName)) {
            init.initialize();
            this.logger.info("Initialized " + pluginName + " bridge.");
        }
        else {
            onFailure.run();
        }
    }

    public RegionProvider getRegionProvider() {
        return this.regionProvider;
    }
}
