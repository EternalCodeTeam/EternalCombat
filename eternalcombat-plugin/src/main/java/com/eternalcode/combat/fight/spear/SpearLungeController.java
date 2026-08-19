package com.eternalcode.combat.fight.spear;

import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.util.DurationUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.UUID;

public class SpearLungeController implements Listener {

    private final SpearService spearService;

    public SpearLungeController(Plugin plugin, FightManager fightManager, SpearService spearService, SpearSettings settings, NoticeService noticeService) {
        this.spearService = spearService;

        try {
            Class<? extends Event> lungeEventClass = Class.forName("io.papermc.paper.event.entity.EntityLungeEvent").asSubclass(Event.class);
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            MethodHandle getEntityHandle = lookup.findVirtual(lungeEventClass, "getEntity", MethodType.methodType(Entity.class));
            MethodHandle setCancelledHandle = lookup.findVirtual(lungeEventClass, "setCancelled", MethodType.methodType(void.class, boolean.class));

            Bukkit.getPluginManager().registerEvent(
                lungeEventClass,
                this,
                EventPriority.NORMAL,
                (listener, event) -> {
                    if (!settings.lungeCooldown || !lungeEventClass.isInstance(event)) {
                        return;
                    }

                    try {
                        Entity entity = (Entity) getEntityHandle.invoke(event);

                        if (!(entity instanceof Player player)) {
                            return;
                        }

                        UUID uuid = player.getUniqueId();

                        if (settings.onlyForFight && !fightManager.isInCombat(uuid)) {
                            return;
                        }

                        if (this.spearService.isOnCooldown(uuid)) {
                            setCancelledHandle.invoke(event, true);

                            noticeService.create()
                                .player(uuid)
                                .notice(settings.lungeOnCooldown)
                                .placeholder("{TIME}", DurationUtil.format(spearService.getRemainingCooldown(uuid), !settings.useMillis))
                                .send();
                        } else {
                            spearService.saveCooldown(uuid);
                        }
                    } catch (Throwable e) {
                        plugin.getLogger().warning("Failed to handle EntityLungeEvent: " + e.getMessage());
                    }
                },
                plugin
            );
        } catch (ClassNotFoundException e) {
            // Silently ignore: Server version is too old to have EntityLungeEvent
        } catch (Throwable e) {
            plugin.getLogger().warning("Failed to hook EntityLungeEvent: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.spearService.removeCooldown(event.getPlayer().getUniqueId());
    }
}
