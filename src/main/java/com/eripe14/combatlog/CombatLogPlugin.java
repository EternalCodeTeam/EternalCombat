package com.eripe14.combatlog;

import com.eripe14.combatlog.combatlog.CombatLogManager;
import com.eripe14.combatlog.command.handler.InvalidUsage;
import com.eripe14.combatlog.command.handler.PermissionMessage;
import com.eripe14.combatlog.command.implementation.TagCommand;
import com.eripe14.combatlog.command.implementation.UnTagCommand;
import com.eripe14.combatlog.config.ConfigFactory;
import com.eripe14.combatlog.config.MessageConfig;
import com.eripe14.combatlog.config.PluginConfig;
import com.eripe14.combatlog.listener.entity.EntityDamageByEntityListener;
import com.eripe14.combatlog.listener.entity.EntityDeathListener;
import com.eripe14.combatlog.listener.player.PlayerCommandPreprocessListener;
import com.eripe14.combatlog.listener.player.PlayerQuitListener;
import com.eripe14.combatlog.message.MessageAnnouncer;
import com.eripe14.combatlog.scheduler.CombatLogManageTask;
import com.eripe14.combatlog.util.legacy.LegacyColorProcessor;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitPlayerArgument;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.stream.Stream;

public class CombatLogPlugin extends JavaPlugin {

    private final File messagePath = new File(this.getDataFolder(), "messages.yml");
    private final File configPath = new File(this.getDataFolder(), "config.yml");

    private MessageConfig messageConfig;
    private PluginConfig pluginConfig;

    private AudienceProvider audienceProvider;
    private MiniMessage miniMessage;

    private MessageAnnouncer messageAnnouncer;

    private CombatLogManager combatLogManager;

    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onLoad() {
        this.messageConfig = new ConfigFactory(messagePath).loadMessageConfig();
        this.pluginConfig = new ConfigFactory(configPath).loadPluginConfig();
    }

    @Override
    public void onEnable() {
        this.combatLogManager = new CombatLogManager();

        this.audienceProvider = BukkitAudiences.create(this);
        this.miniMessage = MiniMessage.builder()
                .postProcessor(new LegacyColorProcessor())
                .build();

        this.messageAnnouncer = new MessageAnnouncer(this.audienceProvider, this.miniMessage);

        this.liteCommands = LiteBukkitFactory.builder(this.getServer(), "eternal-combatlog")
                .argument(Player.class, new BukkitPlayerArgument<>(this.getServer(), this.messageConfig.cantFindPlayer))

                .commandInstance(new TagCommand(this.combatLogManager, this.messageConfig, this.pluginConfig, this.messageAnnouncer))
                .commandInstance(new UnTagCommand(this.combatLogManager, this.messageConfig, this.getServer(), this.messageAnnouncer))

                .invalidUsageHandler(new InvalidUsage(this.messageAnnouncer, this.messageConfig))
                .permissionHandler(new PermissionMessage(this.messageConfig, this.messageAnnouncer))

                .register();

        this.getServer().getScheduler().runTaskTimer(this,
                new CombatLogManageTask(
                        this.combatLogManager,
                        this.messageConfig,
                        this.getServer(),
                        this.messageAnnouncer),
                        20L, 20L);

        Stream.of(
                new EntityDamageByEntityListener(this.combatLogManager, this.messageConfig, this.pluginConfig, this.messageAnnouncer),
                new EntityDeathListener(this.combatLogManager, this.messageConfig, this.getServer(), this.messageAnnouncer),
                new PlayerCommandPreprocessListener(this.combatLogManager, this.pluginConfig, this.messageConfig, this.messageAnnouncer),
                new PlayerQuitListener(this.combatLogManager, this.messageConfig, this.getServer(), this.messageAnnouncer)
        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
    }

    public MiniMessage getMiniMessage() {
        return this.miniMessage;
    }

    public AudienceProvider getAudienceProvider() {
        return this.audienceProvider;
    }

    public MessageAnnouncer getMessageAnnouncer() {
        return this.messageAnnouncer;
    }

    public CombatLogManager getCombatLogManager() {
        return this.combatLogManager;
    }

    public PluginConfig getPluginConfig() {
        return this.pluginConfig;
    }

    public MessageConfig getMessageConfig() {
        return this.messageConfig;
    }

    public LiteCommands<CommandSender> getLiteCommands() {
        return this.liteCommands;
    }
}
