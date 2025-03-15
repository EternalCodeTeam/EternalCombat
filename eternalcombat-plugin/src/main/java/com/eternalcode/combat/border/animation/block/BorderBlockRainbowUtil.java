package com.eternalcode.combat.border.animation.block;

import com.eternalcode.combat.border.BorderPoint;
import com.eternalcode.combat.border.animation.BorderColorUtil;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.google.common.collect.ImmutableMap;
import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;
import org.bukkit.DyeColor;

final class BorderBlockRainbowUtil {

    private final static Map<DyeColor, StateType> GLASSES = new EnumMap<>(ImmutableMap.<DyeColor, StateType>builder()
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

    private final static Map<DyeColor, StateType> TERRACOTTA = new EnumMap<>(ImmutableMap.<DyeColor, StateType>builder()
        .put(DyeColor.WHITE, StateTypes.WHITE_TERRACOTTA)
        .put(DyeColor.ORANGE, StateTypes.ORANGE_TERRACOTTA)
        .put(DyeColor.MAGENTA, StateTypes.MAGENTA_TERRACOTTA)
        .put(DyeColor.LIGHT_BLUE, StateTypes.LIGHT_BLUE_TERRACOTTA)
        .put(DyeColor.YELLOW, StateTypes.YELLOW_TERRACOTTA)
        .put(DyeColor.LIME, StateTypes.LIME_TERRACOTTA)
        .put(DyeColor.PINK, StateTypes.PINK_TERRACOTTA)
        .put(DyeColor.GRAY, StateTypes.GRAY_TERRACOTTA)
        .put(DyeColor.LIGHT_GRAY, StateTypes.LIGHT_GRAY_TERRACOTTA)
        .put(DyeColor.CYAN, StateTypes.CYAN_TERRACOTTA)
        .put(DyeColor.PURPLE, StateTypes.PURPLE_TERRACOTTA)
        .put(DyeColor.BLUE, StateTypes.BLUE_TERRACOTTA)
        .put(DyeColor.BROWN, StateTypes.BROWN_TERRACOTTA)
        .put(DyeColor.GREEN, StateTypes.GREEN_TERRACOTTA)
        .put(DyeColor.RED, StateTypes.RED_TERRACOTTA)
        .build()
    );

    private final static Map<DyeColor, StateType> WOOLS = new EnumMap<>(ImmutableMap.<DyeColor, StateType>builder()
        .put(DyeColor.WHITE, StateTypes.WHITE_WOOL)
        .put(DyeColor.ORANGE, StateTypes.ORANGE_WOOL)
        .put(DyeColor.MAGENTA, StateTypes.MAGENTA_WOOL)
        .put(DyeColor.LIGHT_BLUE, StateTypes.LIGHT_BLUE_WOOL)
        .put(DyeColor.YELLOW, StateTypes.YELLOW_WOOL)
        .put(DyeColor.LIME, StateTypes.LIME_WOOL)
        .put(DyeColor.PINK, StateTypes.PINK_WOOL)
        .put(DyeColor.GRAY, StateTypes.GRAY_WOOL)
        .put(DyeColor.LIGHT_GRAY, StateTypes.LIGHT_GRAY_WOOL)
        .put(DyeColor.CYAN, StateTypes.CYAN_WOOL)
        .put(DyeColor.PURPLE, StateTypes.PURPLE_WOOL)
        .put(DyeColor.BLUE, StateTypes.BLUE_WOOL)
        .put(DyeColor.BROWN, StateTypes.BROWN_WOOL)
        .put(DyeColor.GREEN, StateTypes.GREEN_WOOL)
        .put(DyeColor.RED, StateTypes.RED_WOOL)
        .build()
    );

    private final static Map<DyeColor, StateType> CONCRETE = new EnumMap<>(ImmutableMap.<DyeColor, StateType>builder()
        .put(DyeColor.WHITE, StateTypes.WHITE_CONCRETE)
        .put(DyeColor.ORANGE, StateTypes.ORANGE_CONCRETE)
        .put(DyeColor.MAGENTA, StateTypes.MAGENTA_CONCRETE)
        .put(DyeColor.LIGHT_BLUE, StateTypes.LIGHT_BLUE_CONCRETE)
        .put(DyeColor.YELLOW, StateTypes.YELLOW_CONCRETE)
        .put(DyeColor.LIME, StateTypes.LIME_CONCRETE)
        .put(DyeColor.PINK, StateTypes.PINK_CONCRETE)
        .put(DyeColor.GRAY, StateTypes.GRAY_CONCRETE)
        .put(DyeColor.LIGHT_GRAY, StateTypes.LIGHT_GRAY_CONCRETE)
        .put(DyeColor.CYAN, StateTypes.CYAN_CONCRETE)
        .put(DyeColor.PURPLE, StateTypes.PURPLE_CONCRETE)
        .put(DyeColor.BLUE, StateTypes.BLUE_CONCRETE)
        .put(DyeColor.BROWN, StateTypes.BROWN_CONCRETE)
        .put(DyeColor.GREEN, StateTypes.GREEN_CONCRETE)
        .put(DyeColor.RED, StateTypes.RED_CONCRETE)
        .build()
    );

    static StateType xyzToGlass(BorderPoint point) {
        return GLASSES.get(xyzToDye(point));
    }

    static StateType xyzToTerracotta(BorderPoint point) {
        return TERRACOTTA.get(xyzToDye(point));
    }

    public static StateType xyzToWool(BorderPoint point) {
        return WOOLS.get(xyzToDye(point));
    }

    public static StateType xyzToConcrete(BorderPoint point) {
        return CONCRETE.get(xyzToDye(point));
    }

    private static DyeColor xyzToDye(BorderPoint point) {
        Color rgb = BorderColorUtil.xyzToRainbow(point.x(), point.y(), point.z());
        int distance = Integer.MAX_VALUE;
        DyeColor dye = DyeColor.WHITE;

        for (DyeColor currentDye : DyeColor.values()) {
            org.bukkit.Color color = currentDye.getColor();
            int currentDistance = Math.abs(color.getRed() - rgb.getRed()) + Math.abs(color.getGreen() - rgb.getGreen()) + Math.abs(color.getBlue() - rgb.getBlue());
            if (currentDistance < distance) {
                distance = currentDistance;
                dye = currentDye;
            }
        }
        return dye;
    }
}
