package com.eternalcode.combat;

import com.eternalcode.combat.combat.CombatManager;
import com.eternalcode.combat.combat.CombatTask;
import com.eternalcode.combat.combat.controller.CombatActionBlockerController;
import com.eternalcode.combat.combat.controller.CombatTagController;
import com.eternalcode.combat.combat.controller.CombatUnTagController;
import com.eternalcode.combat.command.InvalidUsage;
import com.eternalcode.combat.command.PermissionMessage;
import com.eternalcode.combat.config.ConfigManager;
import com.eternalcode.combat.config.implementation.PluginConfig;
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
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public final class CombatPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private PluginConfig pluginConfig;

    private CombatManager combatManager;
    private UpdaterService updaterService;

    private AudienceProvider audienceProvider;
    private MiniMessage miniMessage;
    private NotificationAnnouncer notificationAnnouncer;

    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        Stopwatch started = Stopwatch.createStarted();
        Server server = this.getServer();

        this.configManager = new ConfigManager(this.getDataFolder());
        this.pluginConfig = this.configManager.load(new PluginConfig());

        this.combatManager = new CombatManager();
        this.updaterService = new UpdaterService(this.getDescription());

        this.audienceProvider = BukkitAudiences.create(this);
        this.miniMessage = MiniMessage.builder()
            .postProcessor(new LegacyColorProcessor())
            .build();

        this.notificationAnnouncer = new NotificationAnnouncer(this.audienceProvider, this.miniMessage);
        this.liteCommands = LiteBukkitAdventurePlatformFactory.builder(server, "eternalcombat", this.audienceProvider)
            .argument(Player.class, new BukkitPlayerArgument<>(this.getServer(), this.pluginConfig.messages.cantFindPlayer))
            .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>(this.pluginConfig.messages.onlyForPlayers))

            .invalidUsageHandler(new InvalidUsage(this.pluginConfig, this.notificationAnnouncer))
            .permissionHandler(new PermissionMessage(this.pluginConfig, this.notificationAnnouncer))

            .commandInstance(
                new CombatCommand(this.combatManager, this.configManager, this.notificationAnnouncer, this.pluginConfig, server)
            )

            .register();

        CombatTask combatTask = new CombatTask(this.combatManager, this.pluginConfig, server, this.notificationAnnouncer);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, combatTask, 20L, 20L);

        Stream.of(
            new CombatTagController(this.combatManager, this.pluginConfig, this.notificationAnnouncer),
            new CombatUnTagController(this.combatManager, this.pluginConfig, this.notificationAnnouncer),
            new CombatActionBlockerController(this.combatManager, this.notificationAnnouncer, this.pluginConfig),
            new UpdaterNotificationController(this.updaterService, this.pluginConfig, this.audienceProvider, this.miniMessage)
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

        this.combatManager.untagAll();
    }
}
