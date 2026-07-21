package com.eternalcode.combat.fight.spear;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.util.DurationUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.UUID;

public class SpearLungeController implements Listener {

    public SpearLungeController(Plugin plugin, FightManager fightManager, SpearService spearService, PluginConfig settings, NoticeService noticeService) {
        try {
            Class<? extends Event> lungeEventClass = (Class<? extends Event>) Class.forName("io.papermc.paper.event.entity.EntityLungeEvent");

            Method getEntityMethod = lungeEventClass.getMethod("getEntity");
            Method setCancelledMethod = lungeEventClass.getMethod("setCancelled", boolean.class);

            Bukkit.getPluginManager().registerEvent(
                lungeEventClass,
                this,
                EventPriority.NORMAL,
                (listener, event) -> {
                    if (!settings.spear.lungeCooldown) return;
                    if (!lungeEventClass.isInstance(event)) return;

                    try {
                        Object entity = getEntityMethod.invoke(event);

                        if (entity instanceof Player player) {
                            UUID uuid = player.getUniqueId();

                            boolean inCombat = fightManager.isInCombat(uuid);

                            if (settings.spear.onlyForFight && !inCombat) {
                                return;
                            }

                            if (spearService.isOnCooldown(uuid)) {
                                setCancelledMethod.invoke(event, true);

                                Duration remaining = spearService.getRemainingCooldown(uuid);

                                noticeService.create()
                                    .player(uuid)
                                    .notice(settings.spear.lungeOnCooldown)
                                    .placeholder("{TIME}", DurationUtil.format(remaining, !settings.spear.useMillis))
                                    .send();
                            } else {
                                spearService.saveCooldown(uuid);
                            }
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("Failed to handle EntityLungeEvent reflectively: " + e.getMessage());
                    }
                },
                plugin
            );
        } catch (ClassNotFoundException e) {
            plugin.getLogger().info("EntityLungeEvent not found, skipping spear lunge cooldown registration.");
        } catch (NoSuchMethodException e) {
            plugin.getLogger().warning("Failed to find necessary methods for EntityLungeEvent: " + e.getMessage());
        }
    }
}
