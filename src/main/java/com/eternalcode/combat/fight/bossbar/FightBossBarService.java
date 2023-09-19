package com.eternalcode.combat.fight.bossbar;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightTag;
import com.eternalcode.combat.notification.implementation.BossBarNotification;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class FightBossBarService {

    private final PluginConfig pluginConfig;
    private final FightBossBarRegistry bossBarManager;
    private final AudienceProvider audienceProvider;
    private final MiniMessage miniMessage;

    public FightBossBarService(PluginConfig pluginConfig, FightBossBarRegistry bossBarManager, AudienceProvider audienceProvider, MiniMessage miniMessage) {
        this.pluginConfig = pluginConfig;
        this.bossBarManager = bossBarManager;
        this.audienceProvider = audienceProvider;
        this.miniMessage = miniMessage;
    }

    public void show(Player player, FightBossBar fightBossBar) {
        UUID playerUniqueId = player.getUniqueId();

        Audience audience = this.audienceProvider.player(playerUniqueId);
        BossBar bossBar = fightBossBar.bossBar();

        audience.showBossBar(bossBar);
        this.bossBarManager.add(playerUniqueId, fightBossBar);
    }

    public void hide(FightTag fightTag, FightBossBar fightBossBar) {
        Audience audience = fightBossBar.audience();
        BossBar bossBar = fightBossBar.bossBar();
        UUID taggedPlayer = fightTag.getTaggedPlayer();

        audience.hideBossBar(bossBar);
        this.bossBarManager.remove(taggedPlayer);
    }

    public void update(FightTag fightTag, FightBossBar fightBossBar, String message) {
        BossBar bossBar = fightBossBar.bossBar();

        if (fightTag.isExpired()) {
            this.hide(fightTag, fightBossBar);
            return;
        }

        Component name = this.miniMessage.deserialize(message);

        Instant now = Instant.now();
        Instant endOfCombatLog = fightTag.getEndOfCombatLog();

        Duration between = Duration.between(now, endOfCombatLog);
        Duration combatDuration = fightBossBar.combatDuration();

        float difference = (float) between.toMillis() / combatDuration.toMillis();
        float progress = Math.max(0.0F, Math.min(1.0F, difference));

        bossBar.name(name);
        bossBar.progress(progress);
    }

    public void send(Player player, FightTag fightTag, BossBarNotification bossBarNotification, Formatter formatter) {
        UUID playerUniqueId = player.getUniqueId();

        FightBossBar fightBossBar = this.bossBarManager.getFightBossBar(playerUniqueId)
            .orElseGet(() -> this.create(player, bossBarNotification, formatter));

        if (!this.bossBarManager.hasFightBossBar(playerUniqueId)) {
            this.show(player, fightBossBar);
            return;
        }

        String message = formatter.format(bossBarNotification.message());

        this.update(fightTag, fightBossBar, message);
    }

    public FightBossBar create(Player player, BossBarNotification bossBarNotification, Formatter formatter) {
        Audience audience = this.audienceProvider.player(player.getUniqueId());

        Component name = this.miniMessage.deserialize(formatter.format(bossBarNotification.message()));
        BossBar bossBar = bossBarNotification.create(name);

        Duration combatDuration = this.pluginConfig.settings.combatDuration;

        return new FightBossBar(audience, bossBar, combatDuration);
    }
}
