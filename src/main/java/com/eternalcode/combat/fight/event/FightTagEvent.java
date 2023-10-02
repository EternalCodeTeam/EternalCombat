package com.eternalcode.combat.fight.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class FightTagEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final UUID player;

    public FightTagEvent(UUID player) {
        this.player = player;
    }

    public UUID getPlayer() {
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
