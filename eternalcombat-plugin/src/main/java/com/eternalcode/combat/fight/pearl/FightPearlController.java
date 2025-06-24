package com.eternalcode.combat.fight.pearl;

import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.util.DurationUtil;
import java.time.Duration;
import java.util.UUID;
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

public class FightPearlController implements Listener {

    private final FightPearlSettings settings;
    private final NoticeService noticeService;
    private final FightManager fightManager;
    private final FightPearlService fightPearlService;

    public FightPearlController(
        FightPearlSettings settings,
        NoticeService noticeService,
        FightManager fightManager,
        FightPearlService fightPearlService
    ) {
        this.settings = settings;
        this.noticeService = noticeService;
        this.fightManager = fightManager;
        this.fightPearlService = fightPearlService;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPearlThrow(PlayerInteractEvent event) {
        if (!isPearlRightClick(event)) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!fightManager.isInCombat(playerId)) {
            return;
        }

        // Check if pearls are completely disabled during combat
        if (settings.pearlThrowDisabledDuringCombat) {
            event.setCancelled(true);
            noticeService.create()
                .player(playerId)
                .notice(settings.pearlThrowBlockedDuringCombat)
                .send();
            return;
        }

        // Handle cooldown if enabled
        if (settings.pearlCooldownEnabled) {
            handlePearlCooldown(event, player, playerId);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPearlDamage(EntityDamageByEntityEvent event) {
        if (settings.pearlThrowDamageEnabled) {
            return;
        }

        if (!(event.getEntity() instanceof Player) ||
            !(event.getDamager() instanceof EnderPearl) ||
            event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        event.setDamage(0.0);
    }

    private void handlePearlCooldown(PlayerInteractEvent event, Player player, UUID playerId) {
        // If delay is zero, block completely
        if (settings.pearlThrowDelay.isZero()) {
            event.setCancelled(true);
            noticeService.create()
                .player(playerId)
                .notice(settings.pearlThrowBlockedDuringCombat)
                .send();
            return;
        }

        // Check if player has active delay
        if (fightPearlService.hasDelay(playerId)) {
            event.setCancelled(true);
            Duration remainingDelay = fightPearlService.getRemainingDelay(playerId);

            noticeService.create()
                .player(playerId)
                .notice(settings.pearlThrowBlockedDelayDuringCombat)
                .placeholder("{TIME}", DurationUtil.format(remainingDelay))
                .send();
            return;
        }

        // Apply cooldown
        fightPearlService.markDelay(playerId);
        int cooldownTicks = (int) (settings.pearlThrowDelay.toMillis() / 50);
        player.setCooldown(Material.ENDER_PEARL, cooldownTicks);
    }

    private boolean isPearlRightClick(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.ENDER_PEARL) {
            return false;
        }

        Action action = event.getAction();
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }
}
