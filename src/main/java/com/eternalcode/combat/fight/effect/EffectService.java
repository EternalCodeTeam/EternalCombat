package com.eternalcode.combat.fight.effect;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class EffectService {
    private final Map<Player, Map<PotionEffectType, PotionEffect>> activeEffects;

    public EffectService() {
        this.activeEffects = new HashMap<>();
    }

    public void addEffect(Player player, PotionEffectType type, PotionEffect effect) {
        Map<PotionEffectType, PotionEffect> playerEffects = this.activeEffects.getOrDefault(player, new HashMap<>());
        playerEffects.put(type, effect);
        this.activeEffects.put(player, playerEffects);
    }


    public void removeAllEffects(Player player) {
        Map<PotionEffectType, PotionEffect> playerEffects = this.activeEffects.get(player);
        if (playerEffects != null) {
            for (PotionEffectType type : playerEffects.keySet()) {
                player.removePotionEffect(type);
            }
            this.activeEffects.remove(player);
        }
    }

    public Map<PotionEffectType, PotionEffect> getCurrentEffects(Player player) {
        return this.activeEffects.getOrDefault(player, new HashMap<>());
    }

    /*Additional methods:

    public void removeEffect(Player player, PotionEffectType type) {
        Map<PotionEffectType, PotionEffect> playerEffects = this.activeEffects.get(player);
        if (playerEffects != null) {
            PotionEffect removedEffect = playerEffects.remove(type);
            if (removedEffect != null) {
                player.removePotionEffect(type);
            }
        }
    }

    public boolean hasEffect(Player player, PotionEffectType type) {
        Map<PotionEffectType, PotionEffect> playerEffects = this.activeEffects.get(player);
        return playerEffects != null && playerEffects.containsKey(type);
    }

    public void clearAllEffects() {
        this.activeEffects.clear();
    }

    */
}
