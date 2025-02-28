package com.eternalcode.combat.border.animation;

import java.awt.Color;

public final class BorderColorUtil {

    private BorderColorUtil() {
    }

    public static Color xyzToRainbow(int x, int y, int z) {
        float hue = (float) (((Math.sin(x * 0.05) + Math.cos(z * 0.05)) * 0.5 + 0.5) % 1.0);
        float saturation = 1.0f;
        float brightness = 0.8f + 0.2f * Math.max(0.0f, Math.min(1.0f, (float) y / 255));

        return Color.getHSBColor(hue, saturation, brightness);
    }

}
