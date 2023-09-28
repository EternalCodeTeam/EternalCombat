package com.eternalcode.combat.fight.effect;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffectService {
    private final Map<Player, List<PotionEffect>> activeEffects;

    public EffectService() {
        this.activeEffects = new HashMap<>();
    }

    public void addEffect(Player player, PotionEffect effect) {

        this.activeEffects.getOrDefault(player, new ArrayList<>()).add(effect);
        List<PotionEffect> playerEffects = this.activeEffects.getOrDefault(player, new ArrayList<>());
        playerEffects.add(effect);
        this.activeEffects.remove(player);
        this.activeEffects.put(player, playerEffects);
    }


    public void removeAllEffects(Player player) {
        List<PotionEffect> playerEffects = this.activeEffects.get(player);
        if (playerEffects != null) {
            this.activeEffects.remove(player);
        }

    }


    public List<PotionEffect> getCurrentEffects(Player player) {
        return this.activeEffects.getOrDefault(player, new ArrayList<>());
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
