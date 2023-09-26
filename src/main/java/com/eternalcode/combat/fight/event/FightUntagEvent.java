package com.eternalcode.combat.fight.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FightUntagEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;


    public FightUntagEvent(Player player) {
        this.player = player;

    }

    public Player getPlayer() {
        return this.player;
    }


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
