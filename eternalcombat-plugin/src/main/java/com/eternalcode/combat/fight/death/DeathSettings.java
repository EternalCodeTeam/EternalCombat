package com.eternalcode.combat.fight.death;

import com.cryptomorin.xseries.particles.XParticle;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import org.bukkit.FireworkEffect;

public class DeathSettings extends OkaeriConfig {

    @Comment({
        "Settings related to lightning effect upon death",
        "Setting both afterEveryDeath and inCombat to false will disable this feature completely"
    })
    public LightningSettings lightning = new LightningSettings();

    public static class LightningSettings extends OkaeriConfig {

        @Comment("Should lightning spawn on every death?")
        public boolean afterEveryDeath = false;

        @Comment("Should lightning spawn on ONLY deaths in combat?")
        public boolean inCombat = true;
    }

    @Comment({
        "Settings for the Arc Raiders style flare (firework)",
        "Setting both afterEveryDeath and inCombat to false will disable this feature completely"
    })
    public FlareSettings firework = new FlareSettings();

    public static class FlareSettings extends OkaeriConfig {

        @Comment("Should firework (flare) spawn on every death?")
        public boolean afterEveryDeath = false;

        @Comment("Should firework (flare) spawn on ONLY deaths in combat?")
        public boolean inCombat = true;

        @Comment("Power of firework - how long till explosion (please enter positive number)")
        public int power = 2;

        @Comment({
            "The firework (flare) effect type (BALL, BALL_LARGE, STAR, BURST, CREEPER)",
            "Reference: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/FireworkEffect.Type.html"
        })
        public FireworkEffect.Type fireworkType = FireworkEffect.Type.BALL;

        @Comment("Hex color for the firework (flare)")
        public String primaryColor = "#a80022";

        @Comment("Hex color for the fade of firework (flare)")
        public String fadeColor = "#0a0a0a";

        @Comment("Toggle on/off additional particles spawned in the firework (flare) path")
        public boolean particlesEnabled = true;

        @Comment({
            "The main trail particle (e.g., CAMPFIRE_COSY_SMOKE, SMOKE_LARGE)",
            "Reference: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html"
        })
        public XParticle mainParticle = XParticle.CAMPFIRE_COSY_SMOKE;

        @Comment("Count of main particles spawned on each tick")
        public int mainParticleCount = 3;

        @Comment({
            "The secondary trail particle (e.g., CAMPFIRE_COSY_SMOKE, SMOKE_LARGE)",
            "Reference: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html"
        })

        public XParticle secondaryParticle = XParticle.SMALL_FLAME;

        @Comment("Count of secondary particles spawned on each tick")
        public int secondaryParticleCount = 3;

    }
}
