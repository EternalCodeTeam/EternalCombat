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

    private CombatManager combatManager;
    private AudienceProvider audienceProvider;
    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        Stopwatch started = Stopwatch.createStarted();
        Server server = this.getServer();

        ConfigManager configManager = new ConfigManager(this.getDataFolder());
        PluginConfig pluginConfig = configManager.load(new PluginConfig());

        this.combatManager = new CombatManager();
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

            .commandInstance(new CombatCommand(this.combatManager, configManager, notificationAnnouncer, pluginConfig, server))

            .register();

        CombatTask combatTask = new CombatTask(this.combatManager, pluginConfig, server, notificationAnnouncer);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, combatTask, 20L, 20L);

        Stream.of(
            new CombatTagController(this.combatManager, pluginConfig, notificationAnnouncer),
            new CombatUnTagController(this.combatManager, pluginConfig, notificationAnnouncer),
            new CombatActionBlockerController(this.combatManager, notificationAnnouncer, pluginConfig),
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

        this.combatManager.untagAll();
    }
}
