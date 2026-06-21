package com.eternalcode.combat.fight.knockback;

import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.region.RegionProvider;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final AtomicBoolean mountFailureLogged = new AtomicBoolean();

    public KnockbackMountController(NoticeService noticeService, RegionProvider regionProvider, FightManager fightManager) {
        this.noticeService = noticeService;
        this.regionProvider = regionProvider;
        this.fightManager = fightManager;
    }

    public void register(Plugin plugin) {
        Class<? extends Event> eventClass = null;
        for (String candidate : EVENT_CLASS_CANDIDATES) {
            try {
                eventClass = Class.forName(candidate).asSubclass(Event.class);
                break;
            } catch (ClassNotFoundException | ClassCastException ignored) {
            }
        }

        if (eventClass == null) {
            plugin.getLogger().warning("Could not find any EntityMountEvent class; mount protection in regions is disabled.");
            return;
        }

        MethodHandle getMountHandle;
        try {
            getMountHandle = this.resolveGetMountHandle(eventClass);
        } catch (NoSuchMethodException | IllegalAccessException exception) {
            plugin.getLogger().log(Level.WARNING, "EntityMountEvent has incompatible getMount() method; mount protection in regions is disabled.", exception);
            return;
        }

        plugin.getServer().getPluginManager().registerEvent(
            eventClass,
            this,
            EventPriority.HIGHEST,
            (listener, event) -> this.handle(event, getMountHandle, plugin),
            plugin,
            true
        );
    }

    private MethodHandle resolveGetMountHandle(Class<? extends Event> eventClass) throws NoSuchMethodException, IllegalAccessException {
        if (!EntityEvent.class.isAssignableFrom(eventClass) || !Cancellable.class.isAssignableFrom(eventClass)) {
            throw new NoSuchMethodException(eventClass.getName() + " is not a cancellable entity event");
        }

        Method getMount = eventClass.getMethod("getMount");
        if (!Entity.class.isAssignableFrom(getMount.getReturnType())) {
            throw new NoSuchMethodException(eventClass.getName() + "#getMount() does not return Entity");
        }

        return MethodHandles.publicLookup()
            .unreflect(getMount)
            .asType(MethodType.methodType(Entity.class, Event.class));
    }

    private void handle(Event event, MethodHandle getMountHandle, Plugin plugin) {
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
            mount = (Entity) getMountHandle.invokeExact(event);
        } catch (Throwable exception) {
            this.logMountFailure(plugin, exception);
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

    private void logMountFailure(Plugin plugin, Throwable exception) {
        if (!this.mountFailureLogged.compareAndSet(false, true)) {
            return;
        }

        plugin.getLogger().log(Level.WARNING, "Could not read EntityMountEvent mount; mount protection in regions is skipped.", exception);
    }
}
