package com.eternalcode.combat.fight.trident;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.CauseOfTag;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.util.DurationUtil;
import java.time.Duration;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class TridentController implements Listener {

    private final PluginConfig pluginConfig;
    private final NoticeService noticeService;
    private final FightManager fightManager;
    private final TridentService tridentService;
    private final Server server;

    public TridentController(
        PluginConfig pluginConfig,
        NoticeService noticeService,
        FightManager fightManager,
        TridentService tridentService, Server server
    ) {
        this.pluginConfig = pluginConfig;
        this.noticeService = noticeService;
        this.fightManager = fightManager;
        this.tridentService = tridentService;
        this.server = server;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRiptideInteract(PlayerInteractEvent event) {
        if (!this.isRiptideInteract(event)) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!this.fightManager.isInCombat(playerId)) {
            return;
        }

        if (this.pluginConfig.trident.tridentRiptideDisabledDuringCombat) {
            event.setCancelled(true);

            this.noticeService.create()
                .player(playerId)
                .notice(this.pluginConfig.trident.tridentRiptideBlocked)
                .send();
            return;
        }

        if (!this.tridentService.hasDelay(playerId)) {
            return;
        }

        event.setCancelled(true);
    }

    // we need to send message without ignoring canceled event because of riptide cooldown
    @EventHandler(priority = EventPriority.LOWEST)
    public void messageOnRiptideCooldown(PlayerInteractEvent event) {
        if (!this.isRiptideInteract(event)) {
            return;
        }

        UUID playerId = event.getPlayer().getUniqueId();
        if (!this.tridentService.hasDelay(playerId)) {
            return;
        }

        Duration remainingDelay = this.tridentService.getRemainingDelay(playerId);
        this.noticeService.create()
            .player(playerId)
            .notice(this.pluginConfig.trident.tridentRiptideOnCooldown)
            .placeholder("{TIME}", DurationUtil.format(remainingDelay))
            .send();
    }

    @EventHandler(ignoreCancelled = true)
    public void onRiptide(PlayerRiptideEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (this.pluginConfig.trident.tridentRiptideDisabledDuringCombat) {
            return;
        }

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        if (this.tridentService.hasDelay(uniqueId)) {
            return;
        }

        this.tridentService.markDelay(player.getUniqueId());
        player.setCooldown(Material.TRIDENT, (int) this.pluginConfig.trident.tridentRiptideDelay.toMillis() / 50);

        if (this.pluginConfig.trident.tridentRiptideExtendsCombatTag) {
            this.fightManager.tag(uniqueId, this.pluginConfig.settings.combatTimerDuration, CauseOfTag.TRIDENT);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onUntag(FightUntagEvent event) {
        UUID playerId = event.getPlayer();
        this.tridentService.removeDelay(playerId);

        Player player = server.getPlayer(playerId);
        if (player == null) {
            return;
        }

        player.setCooldown(Material.TRIDENT, 0);
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
