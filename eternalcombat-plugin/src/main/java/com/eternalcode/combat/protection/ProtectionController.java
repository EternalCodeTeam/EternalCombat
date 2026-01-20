package com.eternalcode.combat.protection;

import com.eternalcode.combat.fight.event.CancelTagReason;
import com.eternalcode.combat.fight.event.FightTagEvent;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ProtectionController implements Listener {

    private final ProtectionService protectionService;

    public ProtectionController(ProtectionService protectionService) {
        this.protectionService = protectionService;
    }


    void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            // first join
        }


    }

    void onRespawn(PlayerRespawnEvent event) {

    }

    void onDamageTaken(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            UUID uniqueId = player.getUniqueId();
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    void onFightTag(FightTagEvent event) {
        if (this.protectionService.isProtected(event.getPlayer())) {
            event.setCancelReason(CancelTagReason.PROTECTION);
            event.setCancelled(true);
        }
    }
}
