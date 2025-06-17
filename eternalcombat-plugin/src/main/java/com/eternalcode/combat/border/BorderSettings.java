package com.eternalcode.combat.border;

import com.eternalcode.combat.border.animation.block.BlockSettings;
import com.eternalcode.combat.border.animation.particle.ParticleSettings;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.time.Duration;
import org.jetbrains.annotations.ApiStatus;

public class BorderSettings extends OkaeriConfig {

    @Comment("# Border view distance in blocks")
    public double distance = 6.5;

    @Comment({
        " ",
        "# Border block animation settings",
        "# Configure the block animation that appears during combat."
    })
    public BlockSettings block = new BlockSettings();

    @Comment({
        " ",
        "# Border particle animation settings",
        "# Configure the particle animation that appears during combat."
    })
    public ParticleSettings particle = new ParticleSettings();

    @ApiStatus.Internal
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
