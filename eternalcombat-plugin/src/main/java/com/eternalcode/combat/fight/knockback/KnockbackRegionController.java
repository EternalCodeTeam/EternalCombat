package com.eternalcode.combat.fight.knockback;

import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.FightTagEvent;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.region.Region;
import com.eternalcode.combat.region.RegionProvider;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

public class KnockbackRegionController implements Listener {

    private static final String[] ENTITY_MOUNT_EVENT_CLASSES = {
        "org.bukkit.event.entity.EntityMountEvent",
        "org.spigotmc.event.entity.EntityMountEvent"
    };

    private final NoticeService noticeService;
    private final RegionProvider regionProvider;
    private final FightManager fightManager;
    private final KnockbackService knockbackService;
    private final Server server;

    public KnockbackRegionController(NoticeService noticeService, RegionProvider regionProvider, FightManager fightManager, KnockbackService knockbackService, Server server, Plugin plugin) {
        this.noticeService = noticeService;
        this.regionProvider = regionProvider;
        this.fightManager = fightManager;
        this.knockbackService = knockbackService;
        this.server = server;

        this.registerEntityMountEvent(plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Location locationTo = event.getTo();
        int xTo = locationTo.getBlockX();
        int yTo = locationTo.getBlockY();
        int zTo = locationTo.getBlockZ();

        Location locationFrom = event.getFrom();
        int xFrom = locationFrom.getBlockX();
        int yFrom = locationFrom.getBlockY();
        int zFrom = locationFrom.getBlockZ();

        if (xTo != xFrom || yTo != yFrom || zTo != zFrom) {
            Optional<Region> regionOptional = this.regionProvider.getRegion(locationTo);
            if (regionOptional.isEmpty()) {
                return;
            }

            Region region = regionOptional.get();
            if (region.contains(locationFrom)) {
                this.knockbackService.knockback(region, player);
                this.knockbackService.forceKnockbackLater(player, region);
            } else {
                event.setCancelled(true);
                this.knockbackService.knockbackLater(region, player, Duration.ofMillis(50));
            }

            this.noticeService.create()
                .player(player.getUniqueId())
                .notice(config -> config.messagesSettings.cantEnterOnRegion)
                .send();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Location targetLocation = event.getTo();

        if (this.regionProvider.isInRegion(targetLocation)) {
            event.setCancelled(true);
            this.noticeService.create()
                .player(player.getUniqueId())
                .notice(config -> config.messagesSettings.cantEnterOnRegion)
                .send();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onVehicleMove(VehicleMoveEvent event) {
        Location locationTo = event.getTo();
        Location locationFrom = event.getFrom();

        if (locationTo.getBlockX() == locationFrom.getBlockX()
            && locationTo.getBlockY() == locationFrom.getBlockY()
            && locationTo.getBlockZ() == locationFrom.getBlockZ()) {
            return;
        }

        for (Entity passenger : event.getVehicle().getPassengers()) {
            if (!(passenger instanceof Player player)) {
                continue;
            }

            if (!this.fightManager.isInCombat(player.getUniqueId())) {
                continue;
            }

            Optional<Region> regionOptional = this.regionProvider.getRegion(locationTo);
            if (regionOptional.isEmpty()) {
                return;
            }

            Region region = regionOptional.get();
            if (region.contains(locationFrom)) {
                this.knockbackService.knockback(region, player);
                this.knockbackService.forceKnockbackLater(player, region);
            } else {
                this.knockbackService.knockbackLater(region, player, Duration.ofMillis(50));
            }

            this.noticeService.create()
                .player(player.getUniqueId())
                .notice(config -> config.messagesSettings.cantEnterOnRegion)
                .send();
        }
    }

    private void registerEntityMountEvent(Plugin plugin) {
        Optional<Class<? extends Event>> eventClass = this.findEntityMountEventClass();
        if (eventClass.isEmpty()) {
            plugin.getLogger().fine("EntityMountEvent is not available. Mount region protection will be disabled.");
            return;
        }

        EventExecutor executor = this::onEntityMount;
        plugin.getServer().getPluginManager().registerEvent(eventClass.get(), this, EventPriority.HIGHEST, executor, plugin, true);
    }

    private Optional<Class<? extends Event>> findEntityMountEventClass() {
        for (String eventClassName : ENTITY_MOUNT_EVENT_CLASSES) {
            try {
                return Optional.of(Class.forName(eventClassName).asSubclass(Event.class));
            } catch (ClassNotFoundException ignored) {
                // Try the next API package.
            }
        }

        return Optional.empty();
    }

    private void onEntityMount(Listener listener, Event event) {
        if (!(event instanceof EntityEvent entityEvent)) {
            return;
        }

        if (!(entityEvent.getEntity() instanceof Player player)) {
            return;
        }

        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Entity mount = this.getMount(event);
        if (mount == null || !this.regionProvider.isInRegion(mount.getLocation())) {
            return;
        }

        if (event instanceof Cancellable cancellable) {
            cancellable.setCancelled(true);
        }

        this.noticeService.create()
            .player(player.getUniqueId())
            .notice(config -> config.messagesSettings.cantEnterOnRegion)
            .send();
    }

    private Entity getMount(Event event) {
        try {
            Method getMount = event.getClass().getMethod("getMount");
            Object mount = getMount.invoke(event);

            if (mount instanceof Entity entity) {
                return entity;
            }

            return null;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Cannot read mount from " + event.getClass().getName(), exception);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onTag(FightTagEvent event) {
        Player player = this.server.getPlayer(event.getPlayer());
        if (player == null) {
            throw new IllegalStateException("Player cannot be null!");
        }

        Optional<Region> regionOptional = this.regionProvider.getRegion(player.getLocation());
        if (regionOptional.isEmpty()) {
            return;
        }

        Region region = regionOptional.get();
        this.knockbackService.knockback(region, player);
        this.knockbackService.forceKnockbackLater(player, region);

        this.noticeService.create()
            .player(player.getUniqueId())
            .notice(config -> config.messagesSettings.cantEnterOnRegion)
            .send();
    }

}
