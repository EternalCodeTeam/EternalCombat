package com.eternalcode.combat.fight.effect;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;



public class EffectService {
    private final Map<UUID, List<PotionEffect>> activeEffects;

    public EffectService() {
        this.activeEffects = new HashMap<>();
    }

    public void storeActiveEffect(Player player, PotionEffect effect) {
        List<PotionEffect> effects = this.activeEffects.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());
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
        this.activeEffects.remove(player.getUniqueId());
        if (playerEffects != null) {
            this.activeEffects.remove(player.getUniqueId());
        }

    }

    public List<PotionEffect> getCurrentEffects(Player player) {
        return this.activeEffects.getOrDefault(player.getUniqueId(), new ArrayList<>());
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

        if (activeEffect.getDuration() == -1) {
            return;
        }

        this.storeActiveEffect(player, activeEffect);
        player.addPotionEffect(new PotionEffect(type, -1, amplifier));
    }

    public void removeCustomEffect(Player player, PotionEffectType type, Integer amplifier) {
        PotionEffect activeEffect = player.getPotionEffect(type);

        if (activeEffect == null) {
            return;
        }

        if (activeEffect.getAmplifier() != amplifier) {
            return;
        }

        player.removePotionEffect(type);
    }

}
