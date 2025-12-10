package com.eternalcode.combat.fight;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.event.CancelTagReason;
import com.eternalcode.combat.fight.event.FightTagEvent;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FightBypassController implements Listener {

    private static final String BYPASS_PERMISSION = "eternalcombat.bypass";

    private final Server server;
    private final PluginConfig config;

    public FightBypassController(Server server, PluginConfig config) {
        this.server = server;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    void onFightTagEvent(FightTagEvent event) {
        UUID uniqueId = event.getPlayer();

        Player player = this.server.getPlayer(uniqueId);
        if (player == null) {
            return;
        }

        if (player.hasPermission(BYPASS_PERMISSION)) {
            event.setCancelReason(CancelTagReason.PERMISSION_BYPASS);
            event.setCancelled(true);
            return;
        }

        if (this.config.admin.excludeAdminsFromCombat && player.isOp()) {
            event.setCancelReason(CancelTagReason.ADMIN);
            event.setCancelled(true);
            return;
        }

        if (this.config.admin.excludeCreativePlayersFromCombat && player.getGameMode() == GameMode.CREATIVE) {
            event.setCancelReason(CancelTagReason.CREATIVE_MODE);
            event.setCancelled(true);
        }
    }

}
