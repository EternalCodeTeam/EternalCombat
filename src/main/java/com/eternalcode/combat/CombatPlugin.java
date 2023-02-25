package com.eternalcode.combat;

import com.eternalcode.combat.command.InvalidUsage;
import com.eternalcode.combat.command.PermissionMessage;
import com.eternalcode.combat.config.ConfigManager;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.FightTask;
import com.eternalcode.combat.fight.controller.FightActionBlockerController;
import com.eternalcode.combat.fight.controller.FightActionDamageController;
import com.eternalcode.combat.fight.controller.FightTagController;
import com.eternalcode.combat.fight.controller.FightUnTagController;
import com.eternalcode.combat.notification.NotificationAnnouncer;
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

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public final class CombatPlugin extends JavaPlugin {

    private FightManager fightManager;
    private AudienceProvider audienceProvider;
    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        Stopwatch started = Stopwatch.createStarted();
        Server server = this.getServer();

        ConfigManager configManager = new ConfigManager(this.getDataFolder());
        PluginConfig pluginConfig = configManager.load(new PluginConfig());

        this.fightManager = new FightManager();
        UpdaterService updaterService = new UpdaterService(this.getDescription());

        this.audienceProvider = BukkitAudiences.create(this);
        MiniMessage miniMessage = MiniMessage.builder()
            .postProcessor(new LegacyColorProcessor())
            .build();

        NotificationAnnouncer notificationAnnouncer = new NotificationAnnouncer(this.audienceProvider, miniMessage);
        this.liteCommands = LiteBukkitAdventurePlatformFactory.builder(server, "eternalcombat", this.audienceProvider)
            .argument(Player.class, new BukkitPlayerArgument<>(this.getServer(), pluginConfig.messages.cantFindPlayer))
            .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>(pluginConfig.messages.onlyForPlayers))

            .invalidUsageHandler(new InvalidUsage(pluginConfig, notificationAnnouncer))
            .permissionHandler(new PermissionMessage(pluginConfig, notificationAnnouncer))

            .commandInstance(new CombatCommand(this.fightManager, configManager, notificationAnnouncer, pluginConfig))

            .register();

        FightTask fightTask = new FightTask(this.fightManager, pluginConfig, server, notificationAnnouncer);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, fightTask, 20L, 20L);

        new Metrics(this, 17803);

        Stream.of(
            new FightTagController(this.fightManager, pluginConfig, notificationAnnouncer),
            new FightUnTagController(this.fightManager, pluginConfig, notificationAnnouncer),
            new FightActionBlockerController(this.fightManager, notificationAnnouncer, pluginConfig),
            new FightActionDamageController(this.fightManager, pluginConfig, notificationAnnouncer),
            new UpdaterNotificationController(updaterService, pluginConfig, this.audienceProvider, miniMessage)
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
