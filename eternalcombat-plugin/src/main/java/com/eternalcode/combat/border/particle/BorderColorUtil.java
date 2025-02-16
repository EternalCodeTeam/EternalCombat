package com.eternalcode.combat.border.particle;

import com.github.retrooper.packetevents.protocol.color.Color;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.google.common.collect.ImmutableMap;
import java.util.EnumMap;
import java.util.Map;
import org.bukkit.DyeColor;

final class BorderColorUtil {

    private final static Map<DyeColor, StateType> DYE_TO_STATE_TYPE = new EnumMap<>(ImmutableMap.<DyeColor, StateType>builder()
        .put(DyeColor.WHITE, StateTypes.WHITE_STAINED_GLASS)
        .put(DyeColor.ORANGE, StateTypes.ORANGE_STAINED_GLASS)
        .put(DyeColor.MAGENTA, StateTypes.MAGENTA_STAINED_GLASS)
        .put(DyeColor.LIGHT_BLUE, StateTypes.LIGHT_BLUE_STAINED_GLASS)
        .put(DyeColor.YELLOW, StateTypes.YELLOW_STAINED_GLASS)
        .put(DyeColor.LIME, StateTypes.LIME_STAINED_GLASS)
        .put(DyeColor.PINK, StateTypes.PINK_STAINED_GLASS)
        .put(DyeColor.GRAY, StateTypes.GRAY_STAINED_GLASS)
        .put(DyeColor.LIGHT_GRAY, StateTypes.LIGHT_GRAY_STAINED_GLASS)
        .put(DyeColor.CYAN, StateTypes.CYAN_STAINED_GLASS)
        .put(DyeColor.PURPLE, StateTypes.PURPLE_STAINED_GLASS)
        .put(DyeColor.BLUE, StateTypes.BLUE_STAINED_GLASS)
        .put(DyeColor.BROWN, StateTypes.BROWN_STAINED_GLASS)
        .put(DyeColor.GREEN, StateTypes.GREEN_STAINED_GLASS)
        .put(DyeColor.RED, StateTypes.RED_STAINED_GLASS)
        .build()
    );

    private BorderColorUtil() {
    }

    static Color xyzToColor(int x, int y, int z) {
        java.awt.Color rgb = xyzToRGB(x, y, z);
        return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
    }

    static StateType xyzToColoredGlass(int x, int y, int z) {
        java.awt.Color rgb = xyzToRGB(x, y, z);
        return rgbToColoredGlass(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
    }

    private static StateType rgbToColoredGlass(int red, int green, int blue) {
        int distance = Integer.MAX_VALUE;
        DyeColor dye = DyeColor.WHITE;

        for (DyeColor currentDye : DyeColor.values()) {
            org.bukkit.Color color = currentDye.getColor();
            int currentDistance = Math.abs(color.getRed() - red) + Math.abs(color.getGreen() - green) + Math.abs(color.getBlue() - blue);
            if (currentDistance < distance) {
                distance = currentDistance;
                dye = currentDye;
            }
        }

        return DYE_TO_STATE_TYPE.get(dye);
    }

    private static java.awt.Color xyzToRGB(int x, int y, int z) {
        float hue = (float) (((Math.sin(x * 0.05) + Math.cos(z * 0.05)) * 0.5 + 0.5) % 1.0);
        float saturation = 1.0f;
        float brightness = 0.8f + 0.2f * Math.max(0.0f, Math.min(1.0f, (float) y / 255));

        return java.awt.Color.getHSBColor(hue, saturation, brightness);
    }

}
