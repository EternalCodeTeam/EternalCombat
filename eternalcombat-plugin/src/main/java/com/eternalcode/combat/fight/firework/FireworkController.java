package com.eternalcode.combat.fight.firework;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FireworkController implements Listener {

    private final FightManager fightManager;
    private final PluginConfig pluginConfig;

    public FireworkController(FightManager fightManager, PluginConfig pluginConfig) {
        this.fightManager = fightManager;
        this.pluginConfig = pluginConfig;
    }

    @EventHandler
    public void onPlayerUseFirework(PlayerInteractEvent event) {
        if (!this.pluginConfig.combat.disableFireworks) {
            return;
        }

        Player player = event.getPlayer();

        if (!this.fightManager.isInCombat(player.getUniqueId())) {
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
        }
    }
}
