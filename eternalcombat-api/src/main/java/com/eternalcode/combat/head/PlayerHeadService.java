package com.eternalcode.combat.head;

import org.bukkit.entity.Player;

public interface PlayerHeadService {

    /**
     * Attempts to drop a player's head based on configuration settings
     *
     * @param victim The player who died
     * @param killer The player who killed the victim (can be null)
     * @return true if a head was dropped, false otherwise
     */
    boolean tryDropHead(Player victim, Player killer);

} 