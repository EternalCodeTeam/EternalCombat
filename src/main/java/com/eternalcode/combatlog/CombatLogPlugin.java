package com.eternalcode.combatlog;

import com.eternalcode.combatlog.combat.Combat;
import com.eternalcode.combatlog.combat.CombatManager;
import com.eternalcode.combatlog.combat.CombatTask;
import com.eternalcode.combatlog.command.handler.InvalidUsage;
import com.eternalcode.combatlog.command.handler.PermissionMessage;
import com.eternalcode.combatlog.command.implementation.TagCommand;
import com.eternalcode.combatlog.command.implementation.UnTagCommand;
import com.eternalcode.combatlog.config.ConfigManager;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.config.implementation.PluginConfig;
import com.eternalcode.combatlog.listener.InventoryOpenListener;
import com.eternalcode.combatlog.listener.entity.EntityDamageByEntityListener;
import com.eternalcode.combatlog.listener.entity.EntityDeathListener;
import com.eternalcode.combatlog.listener.player.PlayerCommandPreprocessListener;
import com.eternalcode.combatlog.listener.player.PlayerQuitListener;
import com.eternalcode.combatlog.message.MessageAnnouncer;
import com.eternalcode.combatlog.util.legacy.LegacyColorProcessor;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitPlayerArgument;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Stream;

public final class CombatLogPlugin extends JavaPlugin {

    private MessageConfig messageConfig;
    private PluginConfig pluginConfig;

    private AudienceProvider audienceProvider;
    private MiniMessage miniMessage;

    private MessageAnnouncer messageAnnouncer;

    private CombatManager combatManager;

    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        ConfigManager configManager = new ConfigManager(this.getDataFolder());

        this.messageConfig = configManager.load(new MessageConfig());
        this.pluginConfig = configManager.load(new PluginConfig());

        this.audienceProvider = BukkitAudiences.create(this);
        this.miniMessage = MiniMessage.builder()
                .postProcessor(new LegacyColorProcessor())
                .build();

        this.messageAnnouncer = new MessageAnnouncer(this.audienceProvider, this.miniMessage);

        this.combatManager = new CombatManager();

        Server server = this.getServer();

        this.liteCommands = LiteBukkitFactory.builder(server, "eternal-combatlog")
                .argument(Player.class, new BukkitPlayerArgument<>(this.getServer(), this.messageConfig.cantFindPlayer))

                .commandInstance(new TagCommand(this.combatManager, this.messageConfig, this.pluginConfig, this.messageAnnouncer))
                .commandInstance(new UnTagCommand(this.combatManager, this.messageConfig, this.getServer(), this.messageAnnouncer))

                .invalidUsageHandler(new InvalidUsage(this.messageAnnouncer, this.messageConfig))
                .permissionHandler(new PermissionMessage(this.messageConfig, this.messageAnnouncer))

                .register();

        CombatTask combatTask = new CombatTask(this.combatManager, this.messageConfig, server, this.messageAnnouncer);

        this.getServer().getScheduler().runTaskTimer(this, combatTask, 20L, 20L);

        Stream.of(
                new EntityDamageByEntityListener(this.combatManager, this.messageConfig, this.pluginConfig, this.messageAnnouncer),
                new EntityDeathListener(this.combatManager, this.messageConfig, this.getServer(), this.messageAnnouncer),
                new PlayerCommandPreprocessListener(this.combatManager, this.pluginConfig, this.messageConfig, this.messageAnnouncer),
                new PlayerQuitListener(this.combatManager, this.messageConfig, this.getServer(), this.messageAnnouncer),
                new InventoryOpenListener(this.combatManager, this.messageAnnouncer, this.messageConfig)
        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
    }

    @Override
    public void onDisable() {
        this.liteCommands.getPlatform().unregisterAll();
        this.audienceProvider.close();

        for (Combat combat : this.combatManager.getCombats()) {
            this.combatManager.remove(combat.getUuid());
        }
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

    public CombatManager getCombatLogManager() {
        return this.combatManager;
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
