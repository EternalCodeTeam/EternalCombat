package com.eternalcode.combat.border;

import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class BorderParticleController implements Listener {

    private final BorderService borderService;

    public BorderParticleController(BorderService borderService) {
        this.borderService = borderService;
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
                to.getWorld().spawnParticle(Particle.CRIT_MAGIC, borderPoint.x(), borderPoint.y(), borderPoint.z(), 1);
            }
        }
    }

}
