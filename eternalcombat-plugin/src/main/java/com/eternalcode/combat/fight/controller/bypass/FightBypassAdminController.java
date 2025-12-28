package com.eternalcode.combat.fight.controller.bypass;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.event.CancelTagReason;
import com.eternalcode.combat.fight.event.FightTagEvent;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class FightBypassAdminController extends AbstractFightBypassController {

    private final PluginConfig config;

    public FightBypassAdminController(Server server, PluginConfig config) {
        super(server);
        this.config = config;
    }

    @Override
    protected void handleBypass(FightTagEvent event, Player player) {
        if (this.config.admin.excludeAdminsFromCombat && player.isOp()) {
            event.setCancelReason(CancelTagReason.ADMIN);
            event.setCancelled(true);
        }
    }

}
