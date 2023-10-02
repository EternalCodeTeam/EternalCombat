package com.eternalcode.combat.fight.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class FightUntagEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final UUID player;
    private final CauseOfUnTag cause;

    public FightUntagEvent(UUID players, CauseOfUnTag cause) {
        this.player = players;
        this.cause = cause;
    }

    // Gives back UUID of untagged player
    public UUID getPlayer() {
        return this.player;
    }

    // Gives back cause of untag
    public CauseOfUnTag getCause() {
        return this.cause;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
