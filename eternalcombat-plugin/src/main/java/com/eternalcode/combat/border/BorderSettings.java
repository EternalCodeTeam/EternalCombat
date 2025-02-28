package com.eternalcode.combat.border;

import com.eternalcode.combat.border.animation.block.BlockSettings;
import com.eternalcode.combat.border.animation.particle.ParticleSettings;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.time.Duration;

public class BorderSettings extends OkaeriConfig {

    @Comment("# Border view distance")
    public double distance = 6.5;

    @Comment("# Border block animation settings")
    public BlockSettings block = new BlockSettings();

    @Comment("# Border particle animation settings")
    public ParticleSettings particle = new ParticleSettings();

    public Duration indexRefreshDelay() {
        return Duration.ofSeconds(1);
    }

    public int distanceRounded() {
        return (int) Math.ceil(this.distance);
    }

    public boolean isEnabled() {
        return this.block.enabled || this.particle.enabled;
    }

}
