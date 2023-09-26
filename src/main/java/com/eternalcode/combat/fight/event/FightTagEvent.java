package com.eternalcode.combat.fight.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class FightTagEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;


    public FightTagEvent(Player player) {
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
