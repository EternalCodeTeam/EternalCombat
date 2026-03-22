package com.eternalcode.combat.fight.death;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.CauseOfUnTag;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DeathCommandService {

    private final PluginConfig config;
    private final FightManager fightManager;
    private final KillerResolver killerResolver;
    private final DeathCommandExecutor executor;

    private final Map<UUID, String> killerNames = new ConcurrentHashMap<>();
    private final Set<UUID> handledByUntag = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public DeathCommandService(PluginConfig config, FightManager fightManager, KillerResolver killerResolver, DeathCommandExecutor executor) {
        this.config = config;
        this.fightManager = fightManager;
        this.killerResolver = killerResolver;
        this.executor = executor;
    }

    public void handleUntag(Player player, CauseOfUnTag cause) {
        UUID playerUUID = player.getUniqueId();
        
        if (cause == CauseOfUnTag.DEATH || cause == CauseOfUnTag.DEATH_BY_PLAYER) {
            String killerName = this.killerResolver.resolveKillerName(playerUUID, player);
            this.handleDeathInCombat(player, killerName);
            this.handledByUntag.add(playerUUID);
            this.killerNames.put(playerUUID, killerName);
            return;
        }

        this.executor.dispatch(this.config.death.postDeathCommands.onUntag, player, this.config.death.postDeathCommands.unknownKillerPlaceholder);
    }

    public void handleDeath(Player player) {
        UUID playerUUID = player.getUniqueId();
        String killerName = this.killerResolver.resolveKillerName(playerUUID, player);

        this.killerNames.putIfAbsent(playerUUID, killerName);

        this.executor.dispatch(this.config.death.postDeathCommands.onAnyDeath, player, killerName);

        if (this.handledByUntag.remove(playerUUID)) {
            return;
        }

        if (this.fightManager.isInCombat(playerUUID)) {
            this.handleDeathInCombat(player, killerName);
        }
    }

    public void handleRespawn(Player player) {
        UUID playerUUID = player.getUniqueId();

        this.handledByUntag.remove(playerUUID);

        String killerName = this.killerNames.remove(playerUUID);
        if (killerName == null) {
            killerName = this.config.death.postDeathCommands.unknownKillerPlaceholder;
        }

        this.executor.dispatch(this.config.death.postDeathCommands.afterRespawn, player, killerName);
    }

    private void handleDeathInCombat(Player player, String killerName) {
        this.executor.dispatch(this.config.death.postDeathCommands.onDeathInCombat, player, killerName);

        Player killer = this.killerResolver.resolveKiller(player.getUniqueId(), player);
        if (killer != null) {
            this.executor.dispatch(this.config.death.postDeathCommands.killerPostDeathCommands, killer, player.getName(), killerName);
        }
    }
}

