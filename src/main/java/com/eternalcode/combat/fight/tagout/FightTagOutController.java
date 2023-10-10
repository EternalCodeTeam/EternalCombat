package com.eternalcode.combat.fight.tagout;

import com.eternalcode.combat.fight.event.FightTagEvent;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FightTagOutController implements Listener {
    FightTagOutService tagOutService;

    @EventHandler(priority = EventPriority.NORMAL)
    void onTagOut(FightTagEvent event) {
        if (this.tagOutService.isTaggedOut(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    void onTagOut(FightUntagEvent event) {

    }



}
