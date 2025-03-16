package com.eternalcode.combat.border.animation.block;

import com.eternalcode.combat.border.BorderPoint;
import static com.eternalcode.combat.border.animation.block.BorderBlockRainbowUtil.xyzToConcrete;
import static com.eternalcode.combat.border.animation.block.BorderBlockRainbowUtil.xyzToGlass;
import static com.eternalcode.combat.border.animation.block.BorderBlockRainbowUtil.xyzToTerracotta;
import static com.eternalcode.combat.border.animation.block.BorderBlockRainbowUtil.xyzToWool;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import java.util.Locale;

public class BlockType {

    public static final BlockType RAINBOW_GLASS = new BlockType("RAINBOW_GLASS", point -> xyzToGlass(point));
    public static final BlockType RAINBOW_TERRACOTTA = new BlockType("RAINBOW_TERRACOTTA", point -> xyzToTerracotta(point));
    public static final BlockType RAINBOW_WOOL = new BlockType("RAINBOW_WOOL", point -> xyzToWool(point));
    public static final BlockType RAINBOW_CONCRETE = new BlockType("RAINBOW_CONCRETE", point -> xyzToConcrete(point));

    private final String name;
    private final TypeProvider type;

    private BlockType(String name, TypeProvider type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public StateType getStateType(BorderPoint point) {
        return type.provide(point);
    }

    public static BlockType fromName(String name) {
        return switch (name) {
            case "RAINBOW_GLASS" -> RAINBOW_GLASS;
            case "RAINBOW_WOOL" -> RAINBOW_WOOL;
            case "RAINBOW_TERRACOTTA" -> RAINBOW_TERRACOTTA;
            case "RAINBOW_CONCRETE" -> RAINBOW_CONCRETE;
            default -> new BlockType(name, point -> StateTypes.getByName(name.toLowerCase(Locale.ROOT)));
        };
    }

    private interface TypeProvider {
        StateType provide(BorderPoint point);
    }

}