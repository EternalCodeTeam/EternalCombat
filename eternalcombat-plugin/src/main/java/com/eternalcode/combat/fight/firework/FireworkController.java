package com.eternalcode.combat.fight.firework;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class FireworkController implements Listener {

    private final FightManager fightManager;
    private final PluginConfig pluginConfig;
    private final NoticeService noticeService;

    public FireworkController(FightManager fightManager, PluginConfig pluginConfig, NoticeService noticeService) {
        this.fightManager = fightManager;
        this.pluginConfig = pluginConfig;
        this.noticeService = noticeService;
    }

    @EventHandler
    public void onPlayerUseFirework(PlayerInteractEvent event) {
        if (!this.pluginConfig.combat.disableFireworks) {
            return;
        }

        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!player.isGliding()) {
            return;
        }

        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.FIREWORK_ROCKET) {
            event.setCancelled(true);
            this.noticeService.player(uniqueId, config -> this.pluginConfig.messagesSettings.fireworksDisabled);
        }
    }
}
