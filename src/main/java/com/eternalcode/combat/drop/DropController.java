package com.eternalcode.combat.drop;

import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.FightTag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DropController implements Listener {

    private final DropManager dropManager;
    private final DropSettings dropSettings;
    private final FightManager fightManager;

    public DropController(DropManager dropManager, DropSettings dropSettings, FightManager fightManager) {
        this.dropManager = dropManager;
        this.dropSettings = dropSettings;
        this.fightManager = fightManager;
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        DropType dropType = this.dropSettings.dropType;

        if (dropType == DropType.UNCHANGED || !this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        FightTag fightTag = this.fightManager.getTag(player.getUniqueId());

        Drop drop = Drop.builder()
            .player(player)
            .killer(player.getKiller())
            .fightTag(fightTag)
            .deathCause(fightTag.getDeathCause())
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
