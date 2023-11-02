package com.eternalcode.combat;

import com.eternalcode.combat.bridge.BridgeService;
import com.eternalcode.combat.command.InvalidUsage;
import com.eternalcode.combat.command.PermissionMessage;
import com.eternalcode.combat.config.ConfigBackupService;
import com.eternalcode.combat.config.ConfigService;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.drop.DropController;
import com.eternalcode.combat.drop.DropKeepInventoryManager;
import com.eternalcode.combat.drop.DropManager;
import com.eternalcode.combat.drop.impl.PercentDropModifier;
import com.eternalcode.combat.drop.impl.PlayersHealthDropModifier;
import com.eternalcode.combat.event.EventCaller;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.FightTask;
import com.eternalcode.combat.fight.bossbar.FightBossBarService;
import com.eternalcode.combat.fight.controller.FightActionBlockerController;
import com.eternalcode.combat.fight.controller.FightDeathCauseController;
import com.eternalcode.combat.fight.controller.FightEscapeController;
import com.eternalcode.combat.fight.controller.FightMessageController;
import com.eternalcode.combat.fight.controller.FightTagController;
import com.eternalcode.combat.fight.controller.FightUnTagController;
import com.eternalcode.combat.fight.effect.FightEffectController;
import com.eternalcode.combat.fight.effect.FightEffectService;
import com.eternalcode.combat.fight.pearl.FightPearlController;
import com.eternalcode.combat.fight.pearl.FightPearlManager;
import com.eternalcode.combat.fight.tagout.FightTagOutController;
import com.eternalcode.combat.fight.tagout.FightTagOutService;
import com.eternalcode.combat.fight.tagout.TagOutCommand;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.region.RegionController;
import com.eternalcode.combat.region.RegionProvider;
import com.eternalcode.combat.scheduler.BukkitSchedulerImpl;
import com.eternalcode.combat.scheduler.Scheduler;
import com.eternalcode.combat.updater.UpdaterNotificationController;
import com.eternalcode.combat.updater.UpdaterService;
import com.eternalcode.combat.util.legacy.LegacyColorProcessor;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.adventure.platform.LiteBukkitAdventurePlatformFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import dev.rollczi.litecommands.bukkit.tools.BukkitPlayerArgument;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public final class CombatPlugin extends JavaPlugin implements EternalCombatApi {

    private FightManager fightManager;
    private FightPearlManager fightPearlManager;
    private FightTagOutService fightTagOutService;
    private FightEffectService fightEffectService;
    private DropManager dropManager;
    private DropKeepInventoryManager dropKeepInventoryManager;
    private RegionProvider regionProvider;

    private PluginConfig pluginConfig;

    private AudienceProvider audienceProvider;
    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        Stopwatch started = Stopwatch.createStarted();
        Server server = this.getServer();

        File dataFolder = this.getDataFolder();

        ConfigBackupService backupService = new ConfigBackupService(dataFolder);
        ConfigService configService = new ConfigService(backupService);

        EventCaller eventCaller = new EventCaller(server);

        this.pluginConfig = configService.create(PluginConfig.class, new File(dataFolder, "config.yml"));

        this.fightManager = new FightManager(eventCaller);
        this.fightPearlManager = new FightPearlManager(this.pluginConfig.pearl);
        this.fightTagOutService = new FightTagOutService();
        this.fightEffectService = new FightEffectService();
        this.dropManager = new DropManager();
        this.dropKeepInventoryManager = new DropKeepInventoryManager();

        this.audienceProvider = BukkitAudiences.create(this);
        MiniMessage miniMessage = MiniMessage.builder()
            .postProcessor(new LegacyColorProcessor())
            .build();

        UpdaterService updaterService = new UpdaterService(this.getDescription());

        FightBossBarService fightBossBarService = new FightBossBarService(this.pluginConfig, this.audienceProvider, miniMessage);

        BridgeService bridgeService = new BridgeService(this.pluginConfig, server.getPluginManager(), this.getLogger());
        bridgeService.init();

        this.regionProvider = bridgeService.getRegionProvider();

        Scheduler scheduler = new BukkitSchedulerImpl(this, server.getScheduler());

        NotificationAnnouncer notificationAnnouncer = new NotificationAnnouncer(this.audienceProvider, miniMessage, scheduler);

        this.liteCommands = LiteBukkitAdventurePlatformFactory.builder(server, "eternalcombat", this.audienceProvider)
            .argument(Player.class, new BukkitPlayerArgument<>(this.getServer(), this.pluginConfig.messages.playerNotFound))
            .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>(this.pluginConfig.messages.admin.onlyForPlayers))

            .invalidUsageHandler(new InvalidUsage(this.pluginConfig, notificationAnnouncer))
            .permissionHandler(new PermissionMessage(this.pluginConfig, notificationAnnouncer))

            .commandInstance(new CombatCommand(this.fightManager, configService, notificationAnnouncer, this.pluginConfig))
            .commandInstance(new TagOutCommand(this.fightTagOutService, notificationAnnouncer, this.pluginConfig))

            .register();

        FightTask fightTask = new FightTask(server, this.pluginConfig, this.fightManager, fightBossBarService, notificationAnnouncer);
        scheduler.timerSync(fightTask, Duration.ofSeconds(1L), Duration.ofSeconds(1L));

        new Metrics(this, 17803);

        Stream.of(
            new PercentDropModifier(this.pluginConfig.dropSettings),
            new PlayersHealthDropModifier(this.pluginConfig.dropSettings)
        ).forEach(this.dropManager::registerModifier);

        Stream.of(
            new FightDeathCauseController(this.fightManager),
            new DropController(this.dropManager, this.dropKeepInventoryManager, this.pluginConfig.dropSettings, this.fightManager),
            new FightTagController(this.fightManager, this.pluginConfig),
            new FightUnTagController(this.fightManager, this.pluginConfig),
            new FightEscapeController(this.fightManager, this.pluginConfig, notificationAnnouncer),
            new FightActionBlockerController(this.fightManager, notificationAnnouncer, this.pluginConfig),
            new FightPearlController(this.pluginConfig.pearl, notificationAnnouncer, this.fightManager, this.fightPearlManager),
            new UpdaterNotificationController(updaterService, this.pluginConfig, this.audienceProvider, miniMessage),
            new RegionController(notificationAnnouncer, this.regionProvider, this.fightManager, this.pluginConfig),
            new FightEffectController(this.pluginConfig.effect, this.fightEffectService, this.fightManager, this.getServer()),
            new FightTagOutController(this.fightTagOutService, this.pluginConfig),
            new FightMessageController(this.fightManager, notificationAnnouncer, fightBossBarService, this.pluginConfig, this.getServer())
        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));

        EternalCombatProvider.initialize(this);

        long millis = started.elapsed(TimeUnit.MILLISECONDS);
        this.getLogger().info("Successfully loaded EternalCombat in " + millis + "ms");
    }

    @Override
    public void onDisable() {
        EternalCombatProvider.deinitialize();

        if (this.liteCommands != null) {
            this.liteCommands.getPlatform().unregisterAll();
        }

        if (this.audienceProvider != null) {
            this.audienceProvider.close();
        }

        this.fightManager.untagAll();
    }

    @Override
    public FightManager getFightManager() {
        return this.fightManager;
    }

    @Override
    public RegionProvider getRegionProvider() {
        return this.regionProvider;
    }

    @Override
    public FightPearlManager getFightPearlManager() {
        return this.fightPearlManager;
    }

    @Override
    public FightTagOutService getFightTagOutService() {
        return this.fightTagOutService;
    }

    @Override
    public FightEffectService getFightEffectService() {
        return this.fightEffectService;
    }

    @Override
    public DropManager getDropManager() {
        return this.dropManager;
    }

    @Override
    public DropKeepInventoryManager getDropKeepInventoryManager() {
        return this.dropKeepInventoryManager;
    }

    @Override
    public PluginConfig getPluginConfig() {
        return this.pluginConfig;
    }
}
