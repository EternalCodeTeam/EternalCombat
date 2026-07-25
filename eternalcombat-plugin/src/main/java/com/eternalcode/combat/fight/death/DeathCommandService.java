package com.eternalcode.combat.fight.death;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.CauseOfUnTag;
import com.eternalcode.commons.scheduler.Scheduler;
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
    private final Scheduler scheduler;

    private final Map<UUID, String> killerNames = new ConcurrentHashMap<>();
    private final Set<UUID> handledByUntag = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public DeathCommandService(PluginConfig config, FightManager fightManager, KillerResolver killerResolver, DeathCommandExecutor executor, Scheduler scheduler) {
        this.config = config;
        this.fightManager = fightManager;
        this.killerResolver = killerResolver;
        this.executor = executor;
        this.scheduler = scheduler;
    }

    /**
     * scheduleUntagCommands runs in next tick,
     * because we wait for the untag to fully complete,
     * see {@link com.eternalcode.combat.fight.FightManagerImpl#untag(UUID, CauseOfUnTag)}
     */

    public void handleUntag(Player player, CauseOfUnTag cause) {
        if (cause != CauseOfUnTag.DEATH && cause != CauseOfUnTag.DEATH_BY_PLAYER) {
            this.scheduleUntagCommands(player);
            return;
        }

        UUID playerUniqueId = player.getUniqueId();
        String killerName = this.killerResolver.resolveKillerName(playerUniqueId, player);

        this.handledByUntag.add(playerUniqueId);
        this.killerNames.put(playerUniqueId, killerName);
    }

    public void handleDeath(Player player) {
        UUID playerUUID = player.getUniqueId();
        String killerName = this.killerNames.computeIfAbsent(
            playerUUID,
            ignored -> this.killerResolver.resolveKillerName(playerUUID, player)
        );

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

    private void scheduleUntagCommands(Player player) {
        this.scheduler.run(
            () -> this.executor.dispatch(
                this.config.death.postDeathCommands.onUntag,
                player,
                this.config.death.postDeathCommands.unknownKillerPlaceholder
            )
        );
    }
}

