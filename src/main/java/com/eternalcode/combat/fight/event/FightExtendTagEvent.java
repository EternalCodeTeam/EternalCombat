package com.eternalcode.combat.fight.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class FightExtendTagEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final UUID player;
    private final CauseOfTag cause;
    private boolean isCancelled = false;

    public FightExtendTagEvent(UUID player, CauseOfTag cause) {
        this.player = player;
        this.cause = cause;
    }

    // Gives back UUID of tagged player
    public UUID getPlayer() {
        return this.player;
    }

    // Gives back cause of tag
    public CauseOfTag getCause() {
        return this.cause;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
