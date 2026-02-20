package com.eternalcode.combat.fight.death;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class DeathCommandController implements Listener {

    private final DeathCommandService commandService;
    private final PluginConfig config;
    private final FightManager fightManager;

    public DeathCommandController(DeathCommandService commandService, PluginConfig config, FightManager fightManager) {
        this.commandService = commandService;
        this.config = config;
        this.fightManager = fightManager;
    }

    @EventHandler
    void onPlayerUntag(FightUntagEvent event) {
        UUID player = event.getPlayer();

        if (this.config.commands.onlyExecuteIfTagged && )

    }

}
