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
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.FightTask;
import com.eternalcode.combat.fight.controller.FightActionBlockerController;
import com.eternalcode.combat.fight.controller.FightDeathCauseController;
import com.eternalcode.combat.fight.controller.FightEscapeController;
import com.eternalcode.combat.fight.controller.FightTagController;
import com.eternalcode.combat.fight.controller.FightUnTagController;
import com.eternalcode.combat.fight.pearl.FightPearlController;
import com.eternalcode.combat.fight.pearl.FightPearlManager;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.region.RegionController;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public final class CombatPlugin extends JavaPlugin {

    private FightManager fightManager;
    private FightPearlManager fightPearlManager;

    private AudienceProvider audienceProvider;
    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        Stopwatch started = Stopwatch.createStarted();
        Server server = this.getServer();

        File dataFolder = this.getDataFolder();

        ConfigBackupService backupService = new ConfigBackupService(dataFolder);
        ConfigService configService = new ConfigService(backupService);

        PluginConfig pluginConfig = configService.create(PluginConfig.class, new File(dataFolder, "config.yml"));

        this.fightManager = new FightManager();
        this.fightPearlManager = new FightPearlManager(pluginConfig.pearl);

        UpdaterService updaterService = new UpdaterService(this.getDescription());

        this.audienceProvider = BukkitAudiences.create(this);
        MiniMessage miniMessage = MiniMessage.builder()
            .postProcessor(new LegacyColorProcessor())
            .build();

        BridgeService bridgeService = new BridgeService(pluginConfig, server.getPluginManager(), this.getLogger());
        bridgeService.init();

        NotificationAnnouncer notificationAnnouncer = new NotificationAnnouncer(this.audienceProvider, miniMessage);
        this.liteCommands = LiteBukkitAdventurePlatformFactory.builder(server, "eternalcombat", this.audienceProvider)
            .argument(Player.class, new BukkitPlayerArgument<>(this.getServer(), pluginConfig.messages.playerNotFound))
            .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>(pluginConfig.messages.admin.onlyForPlayers))

            .invalidUsageHandler(new InvalidUsage(pluginConfig, notificationAnnouncer))
            .permissionHandler(new PermissionMessage(pluginConfig, notificationAnnouncer))

            .commandInstance(new CombatCommand(this.fightManager, configService, notificationAnnouncer, pluginConfig))

            .register();

        FightTask fightTask = new FightTask(this.fightManager, pluginConfig, server, notificationAnnouncer);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, fightTask, 20L, 20L);

        new Metrics(this, 17803);

        DropManager dropManager = new DropManager();
        DropKeepInventoryManager keepInventoryManager = new DropKeepInventoryManager();

        Stream.of(
            new PercentDropModifier(pluginConfig.dropSettings),
            new PlayersHealthDropModifier(pluginConfig.dropSettings)
        ).forEach(dropManager::registerModifier);

        Stream.of(
            new FightDeathCauseController(this.fightManager),
            new DropController(dropManager, keepInventoryManager, pluginConfig.dropSettings, this.fightManager),
            new FightTagController(this.fightManager, pluginConfig, notificationAnnouncer),
            new FightUnTagController(this.fightManager, pluginConfig, notificationAnnouncer),
            new FightEscapeController(this.fightManager, pluginConfig, notificationAnnouncer),
            new FightActionBlockerController(this.fightManager, notificationAnnouncer, pluginConfig),
            new FightPearlController(pluginConfig.pearl, notificationAnnouncer, this.fightManager, this.fightPearlManager),
            new UpdaterNotificationController(updaterService, pluginConfig, this.audienceProvider, miniMessage),
            new RegionController(notificationAnnouncer, bridgeService.getRegionProvider(), this.fightManager, pluginConfig)
        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));

        long millis = started.elapsed(TimeUnit.MILLISECONDS);
        this.getLogger().info("Successfully loaded EternalCombat in " + millis + "ms");
    }

    @Override
    public void onDisable() {
        if (this.liteCommands != null) {
            this.liteCommands.getPlatform().unregisterAll();
        }

        if (this.audienceProvider != null) {
            this.audienceProvider.close();
        }

        this.fightManager.untagAll();
    }
}
