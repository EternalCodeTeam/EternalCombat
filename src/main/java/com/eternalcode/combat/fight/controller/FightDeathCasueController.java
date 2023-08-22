package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.fight.FightDeathCasue;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.FightTag;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FightDeathCasueController implements Listener {

    private final FightManager fightManager;

    public FightDeathCauseController(FightManager fightManager) {
        this.fightManager = fightManager;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity victimEntity = event.getEntity();
        Entity damagerEntity = event.getDamager();

        if (!(victimEntity instanceof Player victim)) {
            return;
        }

        if (!this.fightManager.isInCombat(victim.getUniqueId())) {
            return;
        }

        if (victim.getHealth() > event.getFinalDamage()) {
            return;
        }

        FightTag fightTag = this.fightManager.getTag(damagerEntity.getUniqueId());

        if (damagerEntity instanceof Player) {
            fightTag.setDeathCasue(FightDeathCasue.KILLED_BY_PLAYER);
            return;
        }

        fightTag.setDeathCasue(FightDeathCasue.KILLED_BY_ENTITY);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        FightTag fightTag = this.fightManager.getTag(player.getUniqueId());
        fightTag.setDeathCasue(FightDeathCasue.ESCAPE);
    }
}
