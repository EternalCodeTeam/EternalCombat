package com.eternalcode.combat.border.animation.particle;

import com.eternalcode.combat.border.BorderPoint;
import com.eternalcode.combat.border.animation.BorderColorUtil;
import java.awt.Color;

public class ParticleColor {

    public static final ParticleColor RAINBOW = new ParticleColor("RAINBOW", point -> BorderColorUtil.xyzToRainbow(point.x(), point.y(), point.z()));

    private final String name;
    private final ColorProvider color;

    private ParticleColor(String name, ColorProvider color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor(BorderPoint point) {
        return color.provide(point);
    }

    public static ParticleColor fromName(String name) {
        if (name.equals(RAINBOW.name)) {
            return RAINBOW;
        }

        return new ParticleColor(name, point -> Color.decode(name));
    }

    private interface ColorProvider {
        Color provide(BorderPoint point);
    }

}
