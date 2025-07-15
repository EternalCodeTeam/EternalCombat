package com.eternalcode.combat.crystalpvp;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import static com.eternalcode.combat.crystalpvp.CrystalPvpConstants.CRYSTAL_METADATA;

public class EndCrystalListener implements Listener {

    private final Plugin plugin;
    private final FightManager fightManager;
    private final PluginConfig pluginConfig;

    public EndCrystalListener(Plugin plugin, FightManager fightManager, PluginConfig pluginConfig) {
        this.plugin = plugin;
        this.fightManager = fightManager;
        this.pluginConfig = pluginConfig;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onPlayerDamageCrystal(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof EnderCrystal enderCrystal) {
            if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player player) {
                enderCrystal.setMetadata(CRYSTAL_METADATA, new CrystalMetadata(this.plugin, player.getUniqueId()));
            }

            if (!(event.getDamager() instanceof Player player)) {
                return;
            }

            enderCrystal.setMetadata(CRYSTAL_METADATA, new CrystalMetadata(this.plugin, player.getUniqueId()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!this.pluginConfig.crystalPvp.tagFromCrystals) {
            return;
        }

        if (pluginConfig.settings.ignoredWorlds.contains(event.getEntity().getWorld().getName())) {
            return;
        }

        Optional<UUID> optionalDamagerUUID = CrystalPvpConstants.getDamagerUniqueIdFromEndCrystal(event);

        if (optionalDamagerUUID.isEmpty()) {
            return;
        }

        if (event.getEntity() instanceof Player player) {
            CrystalPvpConstants.handleCombatTag(
                optionalDamagerUUID,
                player,
                this.fightManager,
                this.pluginConfig
            );
        }
    }

}
