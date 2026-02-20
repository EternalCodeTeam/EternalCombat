package com.eternalcode.combat.fight.death;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class DeathCommandService {

    private final Server server;

    public DeathCommandService(Server server) {
        this.server = server;
    }

    void dispatchAsConsole(String command) {
        this.server.dispatchCommand(this.server.getConsoleSender(), command);
    }

    void dispatchAsPlayer(Player player, String command) {
        this.server.dispatchCommand(player, command);
    }

}
