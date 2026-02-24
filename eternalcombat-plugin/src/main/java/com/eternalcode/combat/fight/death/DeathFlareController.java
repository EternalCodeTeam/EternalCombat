package com.eternalcode.combat.fight.death;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.event.CauseOfUnTag;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import com.eternalcode.commons.scheduler.Scheduler;
import com.eternalcode.commons.scheduler.Task;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.meta.FireworkMeta;

public class DeathFlareController implements Listener {

    private final PluginConfig pluginConfig;
    private final Server server;
    private final Scheduler scheduler;

    public DeathFlareController(PluginConfig pluginConfig, Server server, Scheduler scheduler) {
        this.pluginConfig = pluginConfig;
        this.server = server;
        this.scheduler = scheduler;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFightUntagEvent(FightUntagEvent event) {
        CauseOfUnTag cause = event.getCause();
        if (!cause.equals(CauseOfUnTag.DEATH) && !cause.equals(CauseOfUnTag.DEATH_BY_PLAYER)) {
            return;
        }

        UUID uniqueId = event.getPlayer();
        Player player = this.server.getPlayer(uniqueId);

        if (player == null) {
            return;
        }

        if (this.pluginConfig.death.firework.inCombat && !this.pluginConfig.death.firework.afterEveryDeath) {
            this.spawnFlare(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeathEventLightning(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (this.pluginConfig.death.firework.afterEveryDeath) {
            this.spawnFlare(player);
        }
    }

    private void spawnFlare(Player player) {
        Location deathLocation = player.getLocation();
        World world = deathLocation.getWorld();

        Firework flare = world.spawn(deathLocation, Firework.class);
        FireworkMeta meta = flare.getFireworkMeta();

        Color primaryColor = decodeColor(this.pluginConfig.death.firework.primaryColor, "primary");
        Color fadeColor = decodeColor(this.pluginConfig.death.firework.fadeColor, "fade");

        FireworkEffect effect = FireworkEffect.builder()
                .with(this.pluginConfig.death.firework.fireworkType)
                .withColor(primaryColor)
                .withFade(fadeColor)
                .trail(true)
                .flicker(true)
                .build();

        meta.addEffect(effect);
        meta.setPower(this.pluginConfig.death.firework.power);
        flare.setFireworkMeta(meta);

        if (this.pluginConfig.death.firework.particlesEnabled) {
            scheduleParticles(flare, world);
        }
    }

    private void scheduleParticles(Firework flare, World world) {
        this.scheduler.runLaterAsync(() -> {
            if (flare.isDead() || !flare.isValid()) {
                return;
            }
            this.spawnParticles(world, flare);
            this.scheduleParticles(flare, world);
        }, Duration.ofMillis(50));
    }

    private Color decodeColor(String firework, String name) {
        try {
            return Color.fromRGB(Integer.decode(firework));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid " + name + " format in plugin configuration" + firework, exception);
        }
    }

    private void spawnParticles(World world, Firework flare) {
        Location location = flare.getLocation();

        world.spawnParticle(
                this.pluginConfig.death.firework.mainParticle.get(),
                location,
                this.pluginConfig.death.firework.mainParticleCount,
                0.05,
                0.05,
                0.05,
                0.01
        );

        world.spawnParticle(
                this.pluginConfig.death.firework.secondaryParticle.get(),
                location,
                this.pluginConfig.death.firework.secondaryParticleCount,
                0.1,
                0.1,
                0.1,
                0.01
        );
    }

}
