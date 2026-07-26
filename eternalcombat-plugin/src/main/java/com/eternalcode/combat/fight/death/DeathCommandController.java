package com.eternalcode.combat.fight.death;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.FightTag;
import com.eternalcode.combat.fight.event.CauseOfUnTag;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import com.eternalcode.commons.scheduler.Scheduler;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.UUID;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class DeathCommandController implements Listener {

    private final Cache<UUID, String> killerNames = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(2))
        .build();

    private final PluginConfig config;
    private final FightManager fightManager;
    private final DeathCommandDispatcher dispatcher;
    private final Scheduler scheduler;
    private final Server server;

    public DeathCommandController(
        PluginConfig config,
        FightManager fightManager,
        DeathCommandDispatcher dispatcher,
        Scheduler scheduler,
        Server server
    ) {
        this.config = config;
        this.fightManager = fightManager;
        this.dispatcher = dispatcher;
        this.scheduler = scheduler;
        this.server = server;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onUntag(FightUntagEvent event) {
        Player player = this.server.getPlayer(event.getPlayer());

        if (player == null) {
            return;
        }

        if (!this.isDeath(event.getCause())) {
            this.scheduler.run(() -> this.dispatchUntag(player));
            return;
        }

        Player killer = this.resolveKiller(player);
        String killerName = killer != null ? killer.getName() : this.unknownKiller();

        this.killerNames.put(player.getUniqueId(), killerName);
        this.dispatcher.dispatch(this.config.death.postDeathCommands.onDeathInCombat, player, killerName);

        if (killer != null) {
            this.dispatcher.dispatch(
                this.config.death.postDeathCommands.killerPostDeathCommands,
                killer,
                player.getName(),
                killerName
            );
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        this.dispatcher.dispatch(
            this.config.death.postDeathCommands.onAnyDeath,
            player,
            this.killerNames.get(player.getUniqueId(), ignored -> this.resolveKillerName(player))
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        String killerName = this.killerNames.asMap().remove(player.getUniqueId());

        this.dispatcher.dispatch(
            this.config.death.postDeathCommands.afterRespawn,
            player,
            killerName != null ? killerName : this.unknownKiller()
        );
    }

    @EventHandler
    void onQuit(PlayerQuitEvent event) {
        this.killerNames.invalidate(event.getPlayer().getUniqueId());
    }

    private Player resolveKiller(Player player) {
        Player killer = player.getKiller();

        if (killer != null) {
            return killer;
        }

        FightTag tag = this.fightManager.getTag(player.getUniqueId());
        return tag == null ? null : this.server.getPlayer(tag.getTagger());
    }

    private String resolveKillerName(Player player) {
        Player killer = this.resolveKiller(player);
        return killer != null ? killer.getName() : this.unknownKiller();
    }

    private void dispatchUntag(Player player) {
        if (player.isOnline()) {
            this.dispatcher.dispatch(
                this.config.death.postDeathCommands.onUntag,
                player,
                this.unknownKiller()
            );
        }
    }

    private boolean isDeath(CauseOfUnTag cause) {
        return cause == CauseOfUnTag.DEATH || cause == CauseOfUnTag.DEATH_BY_PLAYER;
    }

    private String unknownKiller() {
        return this.config.death.postDeathCommands.unknownKillerPlaceholder;
    }
}
