package com.eternalcode.combat.fight.tagout;

import com.eternalcode.combat.fight.event.FightTagEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FightTagOutController implements Listener {
    FightTagOutService tagOutService;

    public FightTagOutController(FightTagOutService tagOutService) {
        this.tagOutService = tagOutService;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    void onTagOut(FightTagEvent event) {
        if (this.tagOutService.isTaggedOut(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
