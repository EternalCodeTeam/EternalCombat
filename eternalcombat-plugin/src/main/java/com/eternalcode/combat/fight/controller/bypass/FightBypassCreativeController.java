package com.eternalcode.combat.fight.controller.bypass;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.event.CancelTagReason;
import com.eternalcode.combat.fight.event.FightTagEvent;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class FightBypassCreativeController extends AbstractFightBypassController {

    private final PluginConfig config;

    public FightBypassCreativeController(Server server, PluginConfig config) {
        super(server);
        this.config = config;
    }

    @Override
    protected void handleBypass(FightTagEvent event, Player player) {
        if (this.config.admin.excludeCreativePlayersFromCombat && player.getGameMode() == GameMode.CREATIVE) {
            event.setCancelReason(CancelTagReason.CREATIVE_MODE);
            event.setCancelled(true);
        }
    }

}
