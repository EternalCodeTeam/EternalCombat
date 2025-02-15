package com.eternalcode.combat.border;

import com.eternalcode.commons.scheduler.Scheduler;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleDustData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import java.util.Optional;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class BorderParticleController implements Listener {

    private final BorderService borderService;
    private final Scheduler scheduler;

    public BorderParticleController(BorderService borderService, Scheduler scheduler) {
        this.borderService = borderService;
        this.scheduler = scheduler;
    }

    @EventHandler
    void onMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        Location from = event.getFrom();
        if (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()) {
            return;
        }


        Optional<BorderResult> borderPoints = borderService.resolveBorder(to);

        if (borderPoints.isPresent()) {
            BorderResult result = borderPoints.get();
            for (BorderPoint borderPoint : result) {
                playEffect(event.getPlayer(), borderPoint);
            }
        }

    }

    public void playEffect(Player player, BorderPoint point) {
        scheduler.async(() -> {
            Particle<ParticleDustData> dust = new Particle<>(
                ParticleTypes.DUST,
                new ParticleDustData(1F, xyzToRGB(point.x(), point.y(), point.z()))
            );

            WrapperPlayServerParticle particle = new WrapperPlayServerParticle(
                dust,
                true,
                new Vector3d(
                    point.x(),
                    point.y(),
                    point.z()
                ),
                new Vector3f(0.0f, 0.0f, 0.0f),
                0.0f,
                1,
                true
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, particle);
        });
    }

    public static com.github.retrooper.packetevents.protocol.color.Color xyzToRGB(int x, int y, int z) {
        float hue = (float) (((Math.sin(x * 0.05) + Math.cos(z * 0.05)) * 0.5 + 0.5) % 1.0);

        float saturation = 1.0f;
        float brightness = 0.8f + 0.2f * Math.max(0.0f, Math.min(1.0f, (float) y / 255));

        java.awt.Color hsbColor = java.awt.Color.getHSBColor(hue, saturation, brightness);
        return new com.github.retrooper.packetevents.protocol.color.Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue());
    }

}
