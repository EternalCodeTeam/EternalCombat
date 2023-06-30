package com.eternalcode.combat.fight.pearl;

import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.util.DurationUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import panda.utilities.text.Formatter;

import java.time.Duration;
import java.util.UUID;

public class FightPearlController implements Listener {

    private final FightPearlSettings settings;
    private final NotificationAnnouncer announcer;
    private final FightManager fightManager;
    private final FightPearlManager fightPearlManager;

    public FightPearlController(FightPearlSettings settings, NotificationAnnouncer announcer, FightManager fightManager, FightPearlManager fightPearlManager) {
        this.settings = settings;
        this.announcer = announcer;
        this.fightManager = fightManager;
        this.fightPearlManager = fightPearlManager;
    }

    @EventHandler
    void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

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

        if (!this.settings.enabled) {
            return;
        }

        if (this.settings.delay.isZero()) {
            event.setCancelled(true);
            this.announcer.sendMessage(player, this.settings.pearlThrowBlockedDuringCombat);
            return;
        }

        if (this.fightPearlManager.hasDelay(uniqueId)) {
            event.setCancelled(true);

            Duration remainingPearlDelay = this.fightPearlManager.getRemainingDelay(uniqueId);

            Formatter formatter = new Formatter()
                .register("{TIME}", DurationUtil.format(remainingPearlDelay));

            String format = formatter.format(this.settings.pearlThrowBlockedDelayDuringCombat);
            this.announcer.sendMessage(player, format);
            return;
        }

        this.fightPearlManager.markDelay(uniqueId);
    }
}
