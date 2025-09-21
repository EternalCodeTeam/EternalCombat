package com.eternalcode.combat.region.access;

import com.eternalcode.combat.region.Region;
import org.bukkit.entity.Player;

public interface RegionAccessController {

    boolean canEnter(Player player, Region region);

}

