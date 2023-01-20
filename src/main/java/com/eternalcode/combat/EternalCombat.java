package com.eternalcode.combat;

import com.eternalcode.combat.combat.Combat;
import com.eternalcode.combat.combat.CombatManager;
import com.eternalcode.combat.combat.CombatTask;
import com.eternalcode.combat.command.handler.InvalidUsage;
import com.eternalcode.combat.command.handler.PermissionMessage;
import com.eternalcode.combat.command.implementation.FightCommand;
import com.eternalcode.combat.command.implementation.ReloadCommand;
import com.eternalcode.combat.command.implementation.TagCommand;
import com.eternalcode.combat.command.implementation.UnTagCommand;
import com.eternalcode.combat.config.ConfigManager;
import com.eternalcode.combat.config.implementation.MessageConfig;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.listener.BlockPlaceListener;
import com.eternalcode.combat.listener.InventoryOpenListener;
import com.eternalcode.combat.listener.entity.EntityDamageByEntityListener;
import com.eternalcode.combat.listener.entity.EntityDeathListener;
import com.eternalcode.combat.listener.player.PlayerCommandPreprocessListener;
import com.eternalcode.combat.listener.player.PlayerQuitListener;
import com.eternalcode.combat.updater.UpdaterController;
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

public final class EternalCombat extends JavaPlugin {

    private ConfigManager configManager;

    private MessageConfig messageConfig;
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

        this.messageConfig = configManager.load(new MessageConfig());
        this.pluginConfig = configManager.load(new PluginConfig());

        this.combatManager = new CombatManager();

        this.updaterService = new UpdaterService(this.getDescription());

        this.audienceProvider = BukkitAudiences.create(this);
        this.miniMessage = MiniMessage.builder()
                .postProcessor(new LegacyColorProcessor())
                .build();
        this.notificationAnnouncer = new NotificationAnnouncer(this.audienceProvider, this.miniMessage);

        this.liteCommands = LiteBukkitAdventurePlatformFactory.builder(server, "eternalcombat", this.audienceProvider)
                .argument(Player.class, new BukkitPlayerArgument<>(this.getServer(), this.messageConfig.cantFindPlayer))
                .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>(this.messageConfig.onlyForPlayers))

                .invalidUsageHandler(new InvalidUsage(this.messageConfig, this.notificationAnnouncer))
                .permissionHandler(new PermissionMessage(this.messageConfig, this.notificationAnnouncer))

                .commandInstance(
                        new TagCommand(this.combatManager, this.messageConfig, this.pluginConfig, this.notificationAnnouncer),
                        new UnTagCommand(this.combatManager, this.messageConfig, this.getServer(), this.notificationAnnouncer),
                        new FightCommand(this.combatManager, this.notificationAnnouncer, this.messageConfig),
                        new ReloadCommand(configManager, this.notificationAnnouncer, this.messageConfig)
                )

                .register();

        CombatTask combatTask = new CombatTask(this.combatManager, this.messageConfig, server, this.notificationAnnouncer);

        this.getServer().getScheduler().runTaskTimer(this, combatTask, 20L, 20L);

        Stream.of(
                new EntityDamageByEntityListener(this.combatManager, this.messageConfig, this.pluginConfig, this.notificationAnnouncer),
                new EntityDeathListener(this.combatManager, this.messageConfig, this.getServer(), this.notificationAnnouncer),
                new PlayerCommandPreprocessListener(this.combatManager, this.pluginConfig, this.messageConfig, this.notificationAnnouncer),
                new PlayerQuitListener(this.combatManager, this.messageConfig, this.getServer(), this.notificationAnnouncer),
                new BlockPlaceListener(this.combatManager, this.notificationAnnouncer, this.messageConfig, this.pluginConfig),
                new InventoryOpenListener(this.combatManager, this.notificationAnnouncer, this.messageConfig, this.pluginConfig),
                new UpdaterController(this.updaterService, this.pluginConfig, this.audienceProvider, this.miniMessage)
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

        for (Combat combat : this.combatManager.getCombats()) {
            this.combatManager.remove(combat.getUuid());
        }
    }

}