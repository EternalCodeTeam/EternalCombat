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

    public void storeActiveEffect(Player player, PotionEffect effect) {
        List<PotionEffect> effects = this.activeEffects.computeIfAbsent(player, k -> new ArrayList<>());
        effects.add(effect);

    }

    public void restoreActiveEffects(Player player) {
        List<PotionEffect> effectsFromService = this.getCurrentEffects(player);

        for (PotionEffect effect : effectsFromService) {
            player.addPotionEffect(effect);
        }

        this.clearStoredEffects(player);
    }

    public void clearStoredEffects(Player player) {
        List<PotionEffect> playerEffects = this.activeEffects.get(player);
        if (playerEffects != null) {
            this.activeEffects.remove(player);
        }

    }

    public List<PotionEffect> getCurrentEffects(Player player) {
        return this.activeEffects.getOrDefault(player, new ArrayList<>());
    }

    public void applyCustomEffect(Player player, PotionEffectType type, Integer amplifier) {
        PotionEffect activeEffect = player.getPotionEffect(type);

        if (activeEffect == null) {
            player.addPotionEffect(new PotionEffect(type, -1, amplifier));
            return;
        }

        if (activeEffect.getAmplifier() > amplifier) {
            return;
        }

        this.storeActiveEffect(player, activeEffect);
        player.addPotionEffect(new PotionEffect(type, -1, amplifier));

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
