package com.eternalcode.combat.crystalpvp;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import static com.eternalcode.combat.crystalpvp.CrystalPvpConstants.ANCHOR_METADATA;

public class RespawnAnchorListener implements Listener {

    private final Plugin plugin;
    private final FightManager fightManager;
    private final PluginConfig pluginConfig;


    public RespawnAnchorListener(Plugin plugin, FightManager fightManager, PluginConfig pluginConfig) {
        this.plugin = plugin;
        this.fightManager = fightManager;
        this.pluginConfig = pluginConfig;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onAnchorInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.RESPAWN_ANCHOR) {
            return;
        }

        if (!(block.getBlockData() instanceof RespawnAnchor respawnAnchor)) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        int charges = respawnAnchor.getCharges();

        ItemStack item = event.getItem();
        boolean isGlowstone = item != null && item.getType() == Material.GLOWSTONE;

        if ((charges > 0 && !isGlowstone) || charges == respawnAnchor.getMaximumCharges()) {
            addMetaData(event, block);
        }
    }

    private void addMetaData(PlayerInteractEvent event, Block block) {
        block.setMetadata(
            ANCHOR_METADATA,
            new CrystalMetadata(this.plugin, event.getPlayer().getUniqueId())
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onAnchorExplosion(EntityDamageByBlockEvent event) {
        if (!this.pluginConfig.crystalPvp.tagFromRespawnAnchor) {
            return;
        }

        if (pluginConfig.settings.ignoredWorlds.contains(event.getEntity().getWorld().getName())) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Optional<UUID> optionalDamagerUniqueId = CrystalPvpConstants.getDamagerUniqueIdFromRespawnAnchor(event);

        if (optionalDamagerUniqueId.isEmpty()) {
            return;
        }

        CrystalPvpConstants.handleCombatTag(optionalDamagerUniqueId, player, this.fightManager, this.pluginConfig);
    }

}
