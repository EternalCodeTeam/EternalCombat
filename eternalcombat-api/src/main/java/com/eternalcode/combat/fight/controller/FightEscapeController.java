package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.FightTag;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import panda.utilities.text.Formatter;

public class FightEscapeController implements Listener {

    private final FightManager fightManager;
    private final PluginConfig config;
    private final NotificationAnnouncer announcer;

    public FightEscapeController(FightManager fightManager, PluginConfig config, NotificationAnnouncer announcer) {
        this.fightManager = fightManager;
        this.config = config;
        this.announcer = announcer;
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Formatter formatter = new Formatter()
            .register("{PLAYER}", player.getName());

        this.announcer.broadcast(this.config.messages.playerLoggedOutDuringCombat, formatter);

        FightTag fightTag = this.fightManager.getTag(player.getUniqueId());
        fightTag.setHealthBeforeDeath(player.getHealth());

        player.setHealth(0.0); // Untagged in PlayerDeathEvent
    }
}
