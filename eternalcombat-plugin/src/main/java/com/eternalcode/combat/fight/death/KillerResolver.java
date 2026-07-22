package com.eternalcode.combat.fight.death;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.FightTag;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KillerResolver {

    private final FightManager fightManager;
    private final Server server;
    private final PluginConfig config;

    public KillerResolver(FightManager fightManager, Server server, PluginConfig config) {
        this.fightManager = fightManager;
        this.server = server;
        this.config = config;
    }

    public Player resolveKiller(UUID deadPlayerUUID, Player deadPlayer) {
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

    public String resolveKillerName(UUID deadPlayerUUID, Player deadPlayer) {
        Player killer = this.resolveKiller(deadPlayerUUID, deadPlayer);
        return killer != null ? killer.getName() : this.config.death.postDeathCommands.unknownKillerPlaceholder;
    }
}

