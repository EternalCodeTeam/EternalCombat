package com.eternalcode.combat.fight;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FightPotionEffectManager {

    private final Map<UUID, Collection<PotionEffect>> playersPotionEffects = new HashMap<>();

    public void addPlayerPotionEffects(Player player) {
        UUID uuid = player.getUniqueId();

        this.playersPotionEffects.computeIfAbsent(uuid, k -> player.getActivePotionEffects());
    }

    public void removePlayerPotionEffects(Player player) {
        UUID uuid = player.getUniqueId();

        this.playersPotionEffects.remove(uuid);
    }

    public void restorePotionEffects(Player player) {
        UUID uuid = player.getUniqueId();

        Collection<PotionEffect> potionEffects = this.playersPotionEffects.get(uuid);

        for (PotionEffect potionEffect : potionEffects) {
            player.addPotionEffect(potionEffect);
        }

        this.removePlayerPotionEffects(player);
    }

}
