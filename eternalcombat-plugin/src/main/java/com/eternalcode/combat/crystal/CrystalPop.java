package com.eternalcode.combat.crystal;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.CauseOfTag;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class CrystalPop implements Listener {

    private final Plugin plugin;
    private final FightManager fightManager;
    private final PluginConfig pluginConfig;

    public CrystalPop(Plugin plugin, FightManager fightManager, PluginConfig pluginConfig) {
        this.plugin = plugin;
        this.fightManager = fightManager;
        this.pluginConfig = pluginConfig;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onPlayerDamageCrystal(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof EnderCrystal enderCrystal) {
            if (event.getDamager() instanceof Player player) {
                enderCrystal.setMetadata("damager", new CrystalMetadata(this.plugin, player.getUniqueId()));
            }

            if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player player) {
                enderCrystal.setMetadata("damager", new CrystalMetadata(this.plugin, player.getUniqueId()));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof EnderCrystal enderCrystal && event.getEntity() instanceof Player player) {
            List<MetadataValue> damager = enderCrystal.getMetadata("damager");

            UUID uniqueId = player.getUniqueId();

            Optional<UUID> optionalUniqueId = damager
                    .stream()
                    .filter(source -> source instanceof CrystalMetadata)
                    .map(meta -> (CrystalMetadata) meta)
                    .findFirst()
                    .flatMap(metadata -> metadata.getDamager());

            if (optionalUniqueId.isPresent()) {
                UUID damagerUniqueId = optionalUniqueId.get();
                if (!damagerUniqueId.equals(uniqueId)) {
                    this.fightManager.tag(
                            damagerUniqueId,
                            this.pluginConfig.settings.combatTimerDuration,
                            CauseOfTag.CRYSTAL
                    );
                    this.fightManager.tag(
                            uniqueId,
                            this.pluginConfig.settings.combatTimerDuration,
                            CauseOfTag.CRYSTAL
                    );
                }
            }
        }
    }

}
