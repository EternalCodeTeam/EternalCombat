package com.eternalcode.combat.fight.controller.bypass;

import com.eternalcode.combat.fight.event.CancelTagReason;
import com.eternalcode.combat.fight.event.FightTagEvent;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class FightBypassPermissionController extends AbstractFightBypassController {

    private static final String BYPASS_PERMISSION = "eternalcombat.bypass";

    public FightBypassPermissionController(Server server) {
        super(server);
    }

    @Override
    protected void handleBypass(FightTagEvent event, Player player) {
        if (player.hasPermission(BYPASS_PERMISSION)) {
            event.setCancelReason(CancelTagReason.PERMISSION_BYPASS);
            event.setCancelled(true);
        }
    }

}
