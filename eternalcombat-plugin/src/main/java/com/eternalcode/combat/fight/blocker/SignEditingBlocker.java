package com.eternalcode.combat.fight.blocker;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Event.Result;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SignEditingBlocker implements Listener {

    private final FightManager fightManager;
    private final PluginConfig config;

    public SignEditingBlocker(FightManager fightManager, PluginConfig config) {
        this.fightManager = fightManager;
        this.config = config;
    }

    @EventHandler
    void onInteract(PlayerInteractEvent event) {
        if (!this.config.signEditing.disableSignEditingDuringCombat) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || !isSign(clickedBlock.getType())) {
            return;
        }

        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        event.setUseInteractedBlock(Result.DENY);

        ItemStack item = event.getItem();
        if (item != null && isEnderPearl(item.getType())) {
            event.setUseItemInHand(Result.ALLOW);
        }
    }

    static boolean isSign(Material material) {
        return material.name().endsWith("_SIGN");
    }

    static boolean isEnderPearl(Material material) {
        return material == Material.ENDER_PEARL;
    }
}
