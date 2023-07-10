package com.eternalcode.combat.drop;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DropController implements Listener {

    private final DropManager dropManager;
    private final DropSettings dropSettings;

    public DropController(DropManager dropManager, DropSettings dropSettings) {
        this.dropManager = dropManager;
        this.dropSettings = dropSettings;
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        DropType dropType = this.dropSettings.dropType;

        if (dropType == DropType.UNCHANGED) {
            return;
        }

        Player player = event.getEntity();

        Drop drop = Drop.builder()
            .player(player)
            .killer(player.getKiller())
            .droppedItems(event.getDrops())
            .droppedExp(player.getTotalExperience())
            .build();

        this.dropManager.modify(dropType, drop);

        event.getDrops().clear();
        event.getDrops().addAll(drop.getDroppedItems());

        if (this.dropSettings.affectExperience) {
            event.setDroppedExp(drop.getDroppedExp());
        }
    }
}
