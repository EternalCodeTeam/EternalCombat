package com.eternalcode.combat.fight.trident;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.CauseOfTag;
import com.eternalcode.combat.fight.event.FightTagEvent;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.util.DurationUtil;
import java.time.Duration;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class TridentController implements Listener {

    private static final int TAG_INTERRUPT_COOLDOWN_TICKS = 4;

    private final PluginConfig pluginConfig;
    private final NoticeService noticeService;
    private final FightManager fightManager;
    private final TridentService tridentService;
    private final Server server;

    public TridentController(
        PluginConfig pluginConfig,
        NoticeService noticeService,
        FightManager fightManager,
        TridentService tridentService,
        Server server
    ) {
        this.pluginConfig = pluginConfig;
        this.noticeService = noticeService;
        this.fightManager = fightManager;
        this.tridentService = tridentService;
        this.server = server;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onRiptideInteractLow(PlayerInteractEvent event) {
        this.blockRiptideInteract(event, true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onRiptideInteractHigh(PlayerInteractEvent event) {
        this.blockRiptideInteract(event, false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFightTag(FightTagEvent event) {
        if (!this.pluginConfig.trident.tridentRiptideDisabledDuringCombat) {
            return;
        }

        Player player = this.server.getPlayer(event.getPlayer());
        if (player == null || !player.isHandRaised()) {
            return;
        }

        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        if (!this.isRiptideTrident(mainHandItem) && !this.isRiptideTrident(offHandItem)) {
            return;
        }

        if (this.isRiptideTrident(mainHandItem)) {
            player.getInventory().setItemInMainHand(mainHandItem.clone());
        }

        if (this.isRiptideTrident(offHandItem)) {
            player.getInventory().setItemInOffHand(offHandItem.clone());
        }

        player.updateInventory();
        player.setCooldown(Material.TRIDENT, TAG_INTERRUPT_COOLDOWN_TICKS);

        this.noticeService.create()
            .player(player.getUniqueId())
            .notice(this.pluginConfig.trident.tridentRiptideBlocked)
            .send();
    }

    @EventHandler(ignoreCancelled = true)
    public void onRiptide(PlayerRiptideEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        if (this.pluginConfig.trident.tridentRiptideDisabledDuringCombat) {
            return;
        }

        if (this.tridentService.hasDelay(uniqueId)) {
            return;
        }

        this.tridentService.handleTridentDelay(player);

        if (this.pluginConfig.trident.riptideResetsTimerEnabled) {
            this.fightManager.tag(uniqueId, this.pluginConfig.settings.combatTimerDuration, CauseOfTag.TRIDENT);
        }
    }

    private void blockRiptideInteract(PlayerInteractEvent event, boolean sendNotice) {
        if (!this.isRiptideInteract(event)) {
            return;
        }

        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        if (this.pluginConfig.trident.tridentRiptideDisabledDuringCombat) {
            this.denyUse(event);

            if (sendNotice) {
                this.noticeService.create()
                    .player(uniqueId)
                    .notice(this.pluginConfig.trident.tridentRiptideBlocked)
                    .send();
            }
            return;
        }

        if (!this.tridentService.hasDelay(uniqueId)) {
            return;
        }

        this.denyUse(event);

        if (!sendNotice) {
            return;
        }

        Duration remainingDelay = this.tridentService.getRemainingDelay(uniqueId);
        this.noticeService.create()
            .player(uniqueId)
            .notice(this.pluginConfig.trident.tridentRiptideOnCooldown)
            .placeholder("{TIME}", DurationUtil.format(remainingDelay))
            .send();
    }

    private void denyUse(PlayerInteractEvent event) {
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);
        event.setCancelled(true);
    }

    private boolean isRiptideInteract(PlayerInteractEvent event) {
        EquipmentSlot hand = event.getHand();
        if (hand != EquipmentSlot.HAND && hand != EquipmentSlot.OFF_HAND) {
            return false;
        }

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        return this.isRiptideTrident(event.getItem());
    }

    private boolean isRiptideTrident(ItemStack itemStack) {
        return itemStack != null
            && itemStack.getType() == Material.TRIDENT
            && itemStack.containsEnchantment(Enchantment.RIPTIDE);
    }
}
