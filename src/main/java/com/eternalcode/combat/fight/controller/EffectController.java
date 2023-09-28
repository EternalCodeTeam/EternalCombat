package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.effect.EffectService;
import com.eternalcode.combat.fight.event.FightDeathEvent;
import com.eternalcode.combat.fight.event.FightTagEvent;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;


public class EffectController implements Listener {

    private final EffectService effectService;
    private final FightManager fightManager;
    private final PluginConfig.Settings settings;


    public EffectController(PluginConfig config, EffectService effectService, FightManager fightManager) {
        this.settings = config.settings;
        this.effectService = effectService;
        this.fightManager = fightManager;
    }

    @EventHandler
    public void onTag(FightTagEvent event) {
        if (!this.settings.addInCombatEffects) {
            return;
        }
        Player player = event.getPlayer();

        this.settings.customEffects.forEach((key, value) -> this.effectService.applyCustomEffect(player, key, value));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!this.settings.addInCombatEffects) {
            return;
        }
        Player player = event.getPlayer();
        this.effectService.restoreActiveEffects(player);
    }

    @EventHandler
    public void onUntag(FightUntagEvent event) {
        if (!this.settings.addInCombatEffects) {
            return;
        }
        Player player = event.getPlayer();
        Collection<PotionEffect> activeEffects = player.getActivePotionEffects();

        for (PotionEffect activeEffect : activeEffects) {
            Integer customAmplifier = this.settings.customEffects.get(activeEffect.getType());

            if (customAmplifier == null) {
                continue;
            }

            if (activeEffect.getAmplifier() > customAmplifier) {
                continue;
            }

            player.removePotionEffect(activeEffect.getType());
        }

        this.effectService.restoreActiveEffects(player);
    }

    @EventHandler
    public void onDeath(FightDeathEvent event) {
        if (!this.settings.addInCombatEffects) {
            return;
        }
        Player player = event.getPlayer();
        this.effectService.clearStoredEffects(player);
    }

    @EventHandler
    public void onEffectChange(EntityPotionEffectEvent event) {
        if (!this.settings.addInCombatEffects) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        PotionEffect newEffect = event.getNewEffect();
        PotionEffect oldEffect = event.getOldEffect();

        if (!this.isRemovedEffect(newEffect, oldEffect)) {
            return;
        }

        Integer customAmplifier = this.settings.customEffects.get(oldEffect.getType());

        if (customAmplifier == null) {
            return;
        }

        player.addPotionEffect(new PotionEffect(oldEffect.getType(), -1, customAmplifier));

    }

    private boolean isRemovedEffect(PotionEffect newEffect, PotionEffect oldEffect) {
        return newEffect == null && oldEffect != null;
    }

}
