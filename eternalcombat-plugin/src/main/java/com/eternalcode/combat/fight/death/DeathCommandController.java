package com.eternalcode.combat.fight.death;

import com.eternalcode.combat.config.implementation.CommandSettings;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.FightTag;
import com.eternalcode.combat.fight.event.CauseOfUnTag;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DeathCommandController implements Listener {

    private final PluginConfig config;
    private final FightManager fightManager;
    private final Server server;

    private final Map<UUID, String> killerNames = new ConcurrentHashMap<>();
    private final Set<UUID> handledByUntag = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public DeathCommandController(PluginConfig config, FightManager fightManager, Server server) {
        this.config = config;
        this.fightManager = fightManager;
        this.server = server;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onPlayerUntag(FightUntagEvent event) {
        UUID playerUUID = event.getPlayer();
        CauseOfUnTag cause = event.getCause();
        Player player = this.server.getPlayer(playerUUID);

        if (player == null) {
            return;
        }

        if (cause == CauseOfUnTag.DEATH || cause == CauseOfUnTag.DEATH_BY_PLAYER) {
            String killerName = this.resolveKillerName(playerUUID, player);
            this.handleDeathInCombat(player, killerName);
            this.handledByUntag.add(playerUUID);
            this.killerNames.put(playerUUID, killerName);
            return;
        }
        this.handleUntag(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUniqueId();
        String killerName = this.resolveKillerName(playerUUID, player);

        this.killerNames.putIfAbsent(playerUUID, killerName);

        this.handleAnyDeath(player, killerName);

        if (this.handledByUntag.remove(playerUUID)) {
            return;
        }

        if (this.fightManager.isInCombat(playerUUID)) {
            this.handleDeathInCombat(player, killerName);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        this.handledByUntag.remove(playerUUID);

        String killerName = this.killerNames.remove(playerUUID);
        if (killerName == null) {
            killerName = this.config.commands.unknownKillerPlaceholder;
        }

        this.dispatch(this.config.commands.afterRespawn, player, killerName);
    }

    private void handleDeathInCombat(Player player, String killerName) {
        this.dispatch(this.config.commands.onDeathInCombat, player, killerName);

        Player killer = this.resolveKiller(player.getUniqueId(), player);
        if (killer != null) {
            for (String command : this.config.commands.killerPostDeathCommands) {
                String resolved = this.replacePlaceholders(command, player.getName(), killerName);
                this.server.dispatchCommand(killer, resolved);
            }
        }
    }

    private void handleAnyDeath(Player player, String killerName) {
        this.dispatch(this.config.commands.onAnyDeath, player, killerName);
    }

    private void handleUntag(Player player) {
        this.dispatch(this.config.commands.onUntag, player, this.config.commands.unknownKillerPlaceholder);
    }

    private void dispatch(CommandSettings.PostDeathSettings settings, Player player, String killerName) {
        String playerName = player.getName();
        settings.console.forEach(command -> {
            String resolved = this.replacePlaceholders(command, playerName, killerName);
            this.server.dispatchCommand(this.server.getConsoleSender(), resolved);
        });
        settings.player.forEach(command -> {
            String resolved = this.replacePlaceholders(command, playerName, killerName);
            this.server.dispatchCommand(player, resolved);
        });
    }

    private String resolveKillerName(UUID deadPlayerUUID, Player deadPlayer) {
        Player killer = this.resolveKiller(deadPlayerUUID, deadPlayer);
        return killer != null ? killer.getName() : this.config.commands.unknownKillerPlaceholder;
    }

    private Player resolveKiller(UUID deadPlayerUUID, Player deadPlayer) {
        Player killer = deadPlayer.getKiller();

        if (killer != null) {
            return killer;
        }

        FightTag tag = this.fightManager.getTag(deadPlayerUUID);

        if (tag != null && tag.getTagger() != null) {
            return this.server.getPlayer(tag.getTagger());
        }

        return null;
    }

    private String replacePlaceholders(String command, String playerName, String killerName) {
        return command
            .replace("{player}", playerName)
            .replace("{killer}", killerName);
    }
}
