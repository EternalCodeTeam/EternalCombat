package com.eternalcode.combatlog;

import com.eternalcode.combatlog.combat.Combat;
import com.eternalcode.combatlog.combat.CombatManager;
import com.eternalcode.combatlog.combat.CombatTask;
import com.eternalcode.combatlog.command.handler.InvalidUsage;
import com.eternalcode.combatlog.command.handler.PermissionMessage;
import com.eternalcode.combatlog.command.implementation.FightCommand;
import com.eternalcode.combatlog.command.implementation.ReloadCommand;
import com.eternalcode.combatlog.command.implementation.TagCommand;
import com.eternalcode.combatlog.command.implementation.UnTagCommand;
import com.eternalcode.combatlog.config.ConfigManager;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.config.implementation.PluginConfig;
import com.eternalcode.combatlog.listener.BlockPlaceListener;
import com.eternalcode.combatlog.listener.InventoryOpenListener;
import com.eternalcode.combatlog.listener.entity.EntityDamageByEntityListener;
import com.eternalcode.combatlog.listener.entity.EntityDeathListener;
import com.eternalcode.combatlog.listener.player.PlayerCommandPreprocessListener;
import com.eternalcode.combatlog.listener.player.PlayerQuitListener;
import com.eternalcode.combatlog.util.legacy.LegacyColorProcessor;
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

import java.util.stream.Stream;

public final class EternalCombat extends JavaPlugin {

    private MessageConfig messageConfig;
    private PluginConfig pluginConfig;

    private AudienceProvider audienceProvider;
    private MiniMessage miniMessage;
    private NotificationAnnouncer notificationAnnouncer;
    private CombatManager combatManager;

    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        Server server = this.getServer();

        ConfigManager configManager = new ConfigManager(this.getDataFolder());

        this.messageConfig = configManager.load(new MessageConfig());
        this.pluginConfig = configManager.load(new PluginConfig());

        this.audienceProvider = BukkitAudiences.create(this);
        this.miniMessage = MiniMessage.builder()
                .postProcessor(new LegacyColorProcessor())
                .build();

        this.notificationAnnouncer = new NotificationAnnouncer(this.audienceProvider, this.miniMessage);

        this.combatManager = new CombatManager();

        this.liteCommands = LiteBukkitAdventurePlatformFactory.builder(server, "eternalcombat", this.audienceProvider)
                .argument(Player.class, new BukkitPlayerArgument<>(this.getServer(), this.messageConfig.cantFindPlayer))
                .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>(this.messageConfig.onlyForPlayers))

                .invalidUsageHandler(new InvalidUsage(this.messageConfig, this.notificationAnnouncer))
                .permissionHandler(new PermissionMessage(this.messageConfig, this.notificationAnnouncer))

                .commandInstance(new TagCommand(this.combatManager, this.messageConfig, this.pluginConfig, this.notificationAnnouncer))
                .commandInstance(new UnTagCommand(this.combatManager, this.messageConfig, this.getServer(), this.notificationAnnouncer))
                .commandInstance(new FightCommand(this.combatManager, this.notificationAnnouncer, this.messageConfig))
                .commandInstance(new ReloadCommand(configManager, this.notificationAnnouncer, this.messageConfig))

                .register();

        CombatTask combatTask = new CombatTask(this.combatManager, this.messageConfig, server, this.notificationAnnouncer);

        this.getServer().getScheduler().runTaskTimer(this, combatTask, 20L, 20L);

        Stream.of(
                new EntityDamageByEntityListener(this.combatManager, this.messageConfig, this.pluginConfig, this.notificationAnnouncer),
                new EntityDeathListener(this.combatManager, this.messageConfig, this.getServer(), this.notificationAnnouncer),
                new PlayerCommandPreprocessListener(this.combatManager, this.pluginConfig, this.messageConfig, this.notificationAnnouncer),
                new PlayerQuitListener(this.combatManager, this.messageConfig, this.getServer(), this.notificationAnnouncer),
                new BlockPlaceListener(this.combatManager, this.notificationAnnouncer, this.messageConfig, this.pluginConfig),
                new InventoryOpenListener(this.combatManager, this.notificationAnnouncer, this.messageConfig, this.pluginConfig)
        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
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
