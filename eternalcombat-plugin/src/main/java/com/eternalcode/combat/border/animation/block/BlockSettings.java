package com.eternalcode.combat.border.animation.block;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.time.Duration;

public class BlockSettings extends OkaeriConfig {

    @Comment("# Enable block animation?")
    public boolean enabled = true;

    @Comment({
        "# Block type used for rendering the border",
        "# Custom: RAINBOW_GLASS, RAINBOW_WOOL, RAINBOW_TERRACOTTA, RAINBOW_CONCRETE",
        "# Vanilla: https://javadocs.packetevents.com/com/github/retrooper/packetevents/protocol/world/states/type/StateTypes.html"
    })
    public BlockType type = BlockType.RAINBOW_GLASS;

    @Comment({
        "# Delay between each async animation update",
        "# Lower values will decrease performance but will make the animation smoother",
        "# Higher values will increase performance"
    })
    public Duration updateDelay = Duration.ofMillis(250);

    @Comment({
        "# Delay between each chunk cache update",
        "# Lower values will decrease performance",
        "# Higher values will increase performance but may cause overlapping existing blocks (this does not modify the world)"
    })
    public Duration chunkCacheDelay = Duration.ofMillis(300);

}
