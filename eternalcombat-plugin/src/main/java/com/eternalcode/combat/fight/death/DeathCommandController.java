package com.eternalcode.combat.fight.death;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DeathCommandController implements Listener {

    private final PluginConfig config;
    private final FightManager fightManager;
    private final Server server;

    private final Map<UUID, List<PendingCommand>> pendingCommands = new ConcurrentHashMap<>();
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

        if (cause != CauseOfUnTag.DEATH && cause != CauseOfUnTag.DEATH_BY_PLAYER && cause != CauseOfUnTag.LOGOUT) {
            return;
        }

        Player deadPlayer = this.server.getPlayer(playerUUID);

        if (deadPlayer == null) {
            return;
        }

        this.handledByUntag.add(playerUUID);
        this.executeDeathCommands(deadPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onPlayerDeath(PlayerDeathEvent event) {
        if (this.config.commands.onlyExecuteIfTagged) {
            return;
        }

        Player deadPlayer = event.getEntity();
        UUID playerUUID = deadPlayer.getUniqueId();

        if (this.handledByUntag.remove(playerUUID)) {
            return;
        }

        this.executeDeathCommands(deadPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        this.handledByUntag.remove(playerUUID);

        List<PendingCommand> commands = this.pendingCommands.remove(playerUUID);

        if (commands == null) {
            return;
        }

        for (PendingCommand pending : commands) {
            switch (pending.executor()) {
                case CONSOLE -> this.server.dispatchCommand(this.server.getConsoleSender(), pending.command());
                case DEAD_PLAYER -> this.server.dispatchCommand(player, pending.command());
            }
        }
    }

    private void executeDeathCommands(Player deadPlayer) {
        UUID playerUUID = deadPlayer.getUniqueId();
        String deadPlayerName = deadPlayer.getName();
        String killerName = this.resolveKillerName(playerUUID, deadPlayer);

        List<PendingCommand> deferred = new ArrayList<>();

        for (String command : this.config.commands.consolePostDeathCommands) {
            String resolved = this.replacePlaceholders(command, deadPlayerName, killerName);
            if (this.config.commands.deferConsoleAfterRespawn) {
                deferred.add(new PendingCommand(CommandSource.CONSOLE, resolved));
            } else {
                this.server.dispatchCommand(this.server.getConsoleSender(), resolved);
            }
        }

        for (String command : this.config.commands.deadPostDeathCommands) {
            String resolved = this.replacePlaceholders(command, deadPlayerName, killerName);
            if (this.config.commands.deferDeadAfterRespawn) {
                deferred.add(new PendingCommand(CommandSource.DEAD_PLAYER, resolved));
            } else {
                this.server.dispatchCommand(deadPlayer, resolved);
            }
        }

        Player killer = this.resolveKiller(playerUUID, deadPlayer);

        if (killer != null) {
            for (String command : this.config.commands.killerPostDeathCommands) {
                String resolved = this.replacePlaceholders(command, deadPlayerName, killerName);
                this.server.dispatchCommand(killer, resolved);
            }
        }

        if (!deferred.isEmpty()) {
            this.pendingCommands.put(playerUUID, deferred);
        }
    }


    private String resolveKillerName(UUID deadPlayerUUID, Player deadPlayer) {
        Player killer = this.resolveKiller(deadPlayerUUID, deadPlayer);
        return killer != null ? killer.getName() : "Unknown";
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

    private enum CommandSource {
        CONSOLE,
        DEAD_PLAYER
    }

    private record PendingCommand(CommandSource executor, String command) {
    }
}
