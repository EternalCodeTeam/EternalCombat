package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.effect.EffectService;
import com.eternalcode.combat.fight.event.FightDeathEvent;
import com.eternalcode.combat.fight.event.FightTagEvent;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;


public class EffectController implements Listener {

    private final List<PotionEffect> inCombatEffects;
    private final boolean addInCombatEffects;
    private final EffectService effectService;


    public EffectController(PluginConfig config, EffectService effectService) {
        this.inCombatEffects = config.settings.inCombatEffects;
        this.addInCombatEffects = config.settings.addInCombatEffects;
        this.effectService = effectService;
    }

    @EventHandler
    public void onTag(FightTagEvent event) {
        if (!this.addInCombatEffects) {
            return;
        }
        Player player = event.getPlayer();
        List<PotionEffect> activeEffects = (List<PotionEffect>) player.getActivePotionEffects();

        if (!activeEffects.isEmpty()) {
            for (PotionEffect effect : activeEffects) {
                PotionEffectType type = effect.getType();
                for (PotionEffect inCombatEffect : this.inCombatEffects) {
                    if (type.equals(inCombatEffect.getType())) {
                        this.effectService.addEffect(player, type, effect);
                    }
                }
            }
        }


        for (PotionEffect effect : this.inCombatEffects) {
            if (!activeEffects.contains(effect)) {
                player.addPotionEffect(effect);
            }
        }
    }

    @EventHandler
    public void onUntag(FightUntagEvent event) {
        if (!this.addInCombatEffects) {
            return;
        }
        Player player = event.getPlayer();

        for (PotionEffect effect : this.inCombatEffects) {
            player.removePotionEffect(effect.getType());
        }


        if (!this.effectService.getCurrentEffects(player).isEmpty()) {
            for (PotionEffect effect : this.effectService.getCurrentEffects(player).values()) {
                player.addPotionEffect(effect);
            }
            this.effectService.removeAllEffects(player);
        }
    }

    @EventHandler
    public void onDeath(FightDeathEvent event) {
        this.effectService.removeAllEffects(event.getPlayer());
    }

}
