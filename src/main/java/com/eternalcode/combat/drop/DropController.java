package com.eternalcode.combat.drop;

import com.eternalcode.combat.config.implementation.PluginConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DropController implements Listener {

    private final DropManager dropManager;
    private final PluginConfig config;

    public DropController(DropManager dropManager, PluginConfig config) {
        this.dropManager = dropManager;
        this.config = config;
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        DropType dropType = this.config.settings.dropType;

        if (dropType == DropType.UNCHANGED) {
            return;
        }

        Player player = event.getEntity();

        System.out.println(event.getDroppedExp());

        DropInfo info = DropInfo.builder()
            .player(player)
            .killer(player.getKiller())
            .droppedItems(event.getDrops())
            .droppedExp(player.getTotalExperience())
            .build();

        this.dropManager.modify(dropType, info, this.config);

        event.getDrops().clear();
        event.getDrops().addAll(info.getDroppedItems());

        if (this.config.settings.affectExperience) {
            event.setDroppedExp(info.getDroppedExp());
        }
    }
}
