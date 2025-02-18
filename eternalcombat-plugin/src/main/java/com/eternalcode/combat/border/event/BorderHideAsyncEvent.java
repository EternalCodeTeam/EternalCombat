package com.eternalcode.combat.border.event;

import com.eternalcode.combat.border.BorderPoint;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BorderHideAsyncEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final Set<BorderPoint> points;

    public BorderHideAsyncEvent(Player player, Set<BorderPoint> points) {
        super(true);
        this.player = player;
        this.points = points;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Set<BorderPoint> getPoints() {
        return this.points;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
