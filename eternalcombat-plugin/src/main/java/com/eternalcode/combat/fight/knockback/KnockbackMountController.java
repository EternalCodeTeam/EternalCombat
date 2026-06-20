package com.eternalcode.combat.fight.knockback;

import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.region.RegionProvider;
import java.lang.reflect.Method;
import java.util.logging.Level;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.plugin.Plugin;

// EntityMountEvent moved between org.spigotmc, io.papermc.paper and org.bukkit across versions,
// so we resolve it at runtime to stay compatible without bumping the compile API.
public final class KnockbackMountController implements Listener {

    private static final String[] EVENT_CLASS_CANDIDATES = {
        "org.bukkit.event.entity.EntityMountEvent",
        "io.papermc.paper.event.entity.EntityMountEvent",
        "org.spigotmc.event.entity.EntityMountEvent",
    };

    private final NoticeService noticeService;
    private final RegionProvider regionProvider;
    private final FightManager fightManager;

    public KnockbackMountController(NoticeService noticeService, RegionProvider regionProvider, FightManager fightManager) {
        this.noticeService = noticeService;
        this.regionProvider = regionProvider;
        this.fightManager = fightManager;
    }

    @SuppressWarnings("unchecked")
    public void register(Plugin plugin) {
        Class<? extends Event> eventClass = null;
        for (String candidate : EVENT_CLASS_CANDIDATES) {
            try {
                eventClass = (Class<? extends Event>) Class.forName(candidate);
                break;
            } catch (ClassNotFoundException ignored) {
            }
        }

        if (eventClass == null) {
            plugin.getLogger().warning("Could not find any EntityMountEvent class; mount protection in regions is disabled.");
            return;
        }

        Method getMount;
        try {
            getMount = eventClass.getMethod("getMount");
        } catch (NoSuchMethodException exception) {
            plugin.getLogger().log(Level.WARNING, "EntityMountEvent has no getMount() method; mount protection in regions is disabled.", exception);
            return;
        }

        plugin.getServer().getPluginManager().registerEvent(
            eventClass,
            this,
            EventPriority.HIGHEST,
            (listener, event) -> this.handle(event, getMount),
            plugin,
            true
        );
    }

    private void handle(Event event, Method getMount) {
        if (!(event instanceof EntityEvent entityEvent) || !(event instanceof Cancellable cancellable)) {
            return;
        }

        if (!(entityEvent.getEntity() instanceof Player player)) {
            return;
        }

        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Entity mount;
        try {
            mount = (Entity) getMount.invoke(event);
        } catch (ReflectiveOperationException exception) {
            return;
        }

        if (mount == null || !this.regionProvider.isInRegion(mount.getLocation())) {
            return;
        }

        cancellable.setCancelled(true);
        this.noticeService.create()
            .player(player.getUniqueId())
            .notice(config -> config.messagesSettings.cantEnterOnRegion)
            .send();
    }
}
