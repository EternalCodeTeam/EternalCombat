package com.eternalcode.combat.crystalpvp;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.CauseOfTag;
import com.eternalcode.combat.util.ReflectUtil;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.MetadataValue;

public class CrystalPvpConstants {

    private CrystalPvpConstants() {
    }

    public static final String CRYSTAL_METADATA = "eternalcombat:crystal";
    public static final String ANCHOR_METADATA = "eternalcombat:anchor";

    private static final boolean HAS_DAMAGER_BLOCK_STATE = checkForDamagerBlockState();

    private static boolean checkForDamagerBlockState() {
        try {
            return EntityDamageByBlockEvent.class.getDeclaredMethod("getDamagerBlockState") != null;
        }
        catch (NoSuchMethodException exception) {
            return false;
        }
    }

    static boolean hasDamagerBlockState() {
        return HAS_DAMAGER_BLOCK_STATE;
    }

    public static Optional<UUID> getDamagerUniqueIdFromEndCrystal(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof EnderCrystal enderCrystal) {
            List<MetadataValue> metadataValues = enderCrystal.getMetadata(CRYSTAL_METADATA);
            return metadataValues
                .stream()
                .filter(source -> source instanceof CrystalMetadata)
                .map(meta -> (CrystalMetadata) meta)
                .findFirst()
                .flatMap(CrystalMetadata::getDamager);
        }
        return Optional.empty();
    }

    public static Optional<UUID> getDamagerUniqueIdFromRespawnAnchor(EntityDamageByBlockEvent event) {
        if (!CrystalPvpConstants.hasDamagerBlockState()) {
            return Optional.empty();
        }

        Object maybeState = ReflectUtil.invokeMethod(event, "getDamagerBlockState");
        if (!(maybeState instanceof BlockState state)) {
            return Optional.empty();
        }
        Material type = state.getType();
        if (!type.equals(Material.RESPAWN_ANCHOR)) {
            return Optional.empty();
        }

        return state.getMetadata(ANCHOR_METADATA).stream()
            .filter(source -> source instanceof CrystalMetadata)
            .map(meta -> (CrystalMetadata) meta)
            .findFirst()
            .flatMap(metadata -> metadata.getDamager());
    }

    static void handleCombatTag(
        Optional<UUID> optionalDamagerUUID,
        Player player,
        FightManager fightManager,
        PluginConfig pluginConfig
    ) {
        UUID victimUniqueId = player.getUniqueId();

        if (optionalDamagerUUID.isPresent()) {
            UUID damagerUniqueId = optionalDamagerUUID.get();
            if (!damagerUniqueId.equals(victimUniqueId)) {
                fightManager.tag(
                    damagerUniqueId,
                    pluginConfig.settings.combatTimerDuration,
                    CauseOfTag.CRYSTAL
                );
                fightManager.tag(
                    victimUniqueId,
                    pluginConfig.settings.combatTimerDuration,
                    CauseOfTag.CRYSTAL
                );
            }
        }
    }
}
