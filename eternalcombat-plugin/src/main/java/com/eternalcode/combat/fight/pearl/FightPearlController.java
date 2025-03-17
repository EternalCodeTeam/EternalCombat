package com.eternalcode.combat.fight.pearl;

import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.util.DurationUtil;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.UUID;

public class FightPearlController implements Listener {

    private final FightPearlSettings settings;
    private final NoticeService announcer;
    private final FightManager fightManager;
    private final FightPearlService fightPearlService;

    public FightPearlController(FightPearlSettings settings, NoticeService announcer, FightManager fightManager, FightPearlService fightPearlService) {
        this.settings = settings;
        this.announcer = announcer;
        this.fightManager = fightManager;
        this.fightPearlService = fightPearlService;
    }

    @EventHandler
    void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.settings.pearlThrowBlocked) {
            return;
        }

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.ENDER_PEARL) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (this.settings.pearlThrowDelay.isZero()) {
            event.setCancelled(true);
            this.announcer.create()
                .player(uniqueId)
                .notice(this.settings.pearlThrowBlockedDuringCombat)
                .send();

            return;
        }

        if (this.fightPearlService.hasDelay(uniqueId)) {
            event.setCancelled(true);

            Duration remainingPearlDelay = this.fightPearlService.getRemainingDelay(uniqueId);

            this.announcer.create()
                .player(uniqueId)
                .notice(this.settings.pearlThrowBlockedDelayDuringCombat)
                .placeholder("{TIME}", DurationUtil.format(remainingPearlDelay))
                .send();

            return;
        }

        this.fightPearlService.markDelay(uniqueId);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    void onEntityDamage(EntityDamageByEntityEvent event) {
        if (this.settings.pearlThrowDamageEnabled) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof EnderPearl)) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        event.setDamage(0.0D);
    }
}
